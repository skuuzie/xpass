package com.skuuzie.xpass.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "this_user")

class UserPreferences @Inject constructor(@ApplicationContext appContext: Context) {
    private val datastore = appContext.datastore
    private val USER_NAME = stringPreferencesKey("name") // plaintext
    private val MASTER_KEY = byteArrayPreferencesKey("dk") // encrypted by USER_KEY
    private val USER_KEY = byteArrayPreferencesKey("ukh") // hashed

    val username: Flow<String> =
        datastore.data.map {
            it[USER_NAME] ?: ""
        }

    val userKey: Flow<ByteArray> =
        datastore.data.map {
            it[USER_KEY] ?: ByteArray(0)
        }

    val masterKey: Flow<ByteArray> =
        datastore.data.map {
            it[MASTER_KEY] ?: ByteArray(0)
        }

    suspend fun updateUserName(name: String) {
        datastore.edit { data ->
            data[USER_NAME] = name
        }
    }

    suspend fun updateMasterKey(deviceKey: ByteArray) {
        datastore.edit { data ->
            data[MASTER_KEY] = deviceKey
        }
    }

    suspend fun updateUserKey(userKeyHash: ByteArray) {
        datastore.edit { data ->
            data[USER_KEY] = userKeyHash
        }
    }
}