package com.skuuzie.xpass.data.local

import com.skuuzie.xpass.data.local.database.Credential
import com.skuuzie.xpass.data.local.database.CredentialDao
import com.skuuzie.xpass.util.CredentialHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CredentialRepository {
    val mCredentials: Flow<List<Credential>>

    suspend fun addCredential(credential: Credential)

    suspend fun editCredential(credential: Credential)

    suspend fun deleteCredentialById(id: String)
}

class DefaultCredentialRepository @Inject constructor(
    private val credentialDao: CredentialDao
) : CredentialRepository {

    override val mCredentials: Flow<List<Credential>> =
        credentialDao.getAllCredential().map { credential ->
            credential.filter {
                CredentialHelper.decryptCredential(it)
                true
            }
        }

    override suspend fun addCredential(credential: Credential) {
        CredentialHelper.encryptCredential(credential)
        credentialDao.insertCredential(credential)
    }

    override suspend fun editCredential(credential: Credential) {
        CredentialHelper.encryptCredential(credential)
        credentialDao.updateCredential(credential)
    }

    override suspend fun deleteCredentialById(id: String) {
        credentialDao.deleteCredentialById(id)
    }
}