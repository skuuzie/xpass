package com.skuuzie.xpass.data.datastore

import com.skuuzie.xpass.util.XCrypto
import godroidguard.Godroidguard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class DatastoreManager(
    private val userPreferences: UserPreferences
) {
    var currentUserUsername: Flow<String> = userPreferences.username
    var currentUserKey: Flow<ByteArray> = userPreferences.userKey
    var currentMasterKey: Flow<ByteArray> = userPreferences.masterKey

    // Load user input password if verified
    suspend fun loadUserPassword(password: String): Boolean {
        val inp = Godroidguard.genericHash(password.encodeToByteArray())
        val real = currentUserKey.first()

        if (real.contentEquals(inp)) {
            XCrypto.setKey(XCrypto.getSurfaceKey())
            val upass = Godroidguard.authenticatedHash(password.encodeToByteArray())
            XCrypto.setKey(upass)

            val masterKey = Godroidguard.decrypt(currentMasterKey.first())
            XCrypto.setKey(masterKey)
            return true
        } else {
            return false
        }
    }

    suspend fun updateUserName(name: String) {
        return userPreferences.updateUserName(name)
    }

    suspend fun updateUserPassword(password: String) {
        // Hash password for future verification
        val hashedPassword = Godroidguard.genericHash(password.encodeToByteArray())
        userPreferences.updateUserKey(hashedPassword)

        // Immediately change the current key with the new password
        XCrypto.setKey(XCrypto.getSurfaceKey())
        val upass = Godroidguard.authenticatedHash(password.encodeToByteArray())
        XCrypto.setKey(upass)

        // Immediately change the master key, encrypted with the new password
        val masterKey = Godroidguard.getRandomBytes(128)
        val encMasterKey = Godroidguard.encrypt(masterKey)
        userPreferences.updateMasterKey(encMasterKey)

        XCrypto.setKey(masterKey)
    }
}