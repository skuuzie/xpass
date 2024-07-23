package com.skuuzie.xpass.util

import com.skuuzie.xpass.data.local.database.Credential

object CredentialHelper {
    fun decryptCredential(credential: Credential) {
        credential.email = XCrypto.decryptBase64(credential.email).decodeToString()
        credential.username = XCrypto.decryptBase64(credential.username).decodeToString()
        credential.platform = XCrypto.decryptBase64(credential.platform).decodeToString()
        credential.password = XCrypto.decryptBase64(credential.password).decodeToString()
    }

    fun encryptCredential(credential: Credential) {
        credential.email = XCrypto.encryptBase64(credential.email.encodeToByteArray())
        credential.username = XCrypto.encryptBase64(credential.username.encodeToByteArray())
        credential.platform = XCrypto.encryptBase64(credential.platform.encodeToByteArray())
        credential.password = XCrypto.encryptBase64(credential.password.encodeToByteArray())
    }

    fun filterCredential(credential: Credential) {
        if (credential.platform.isEmpty()) credential.platform = "-"
        if (credential.email.isEmpty()) credential.email = "-"
        if (credential.username.isEmpty()) credential.username = "-"
        if (credential.password.isEmpty()) credential.password = "-"
    }
}