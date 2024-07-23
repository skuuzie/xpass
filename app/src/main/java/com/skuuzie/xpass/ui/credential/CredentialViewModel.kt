package com.skuuzie.xpass.ui.credential

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.skuuzie.xpass.data.local.CredentialRepository
import com.skuuzie.xpass.data.local.database.Credential
import com.skuuzie.xpass.util.CredentialHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CredentialViewModel @Inject constructor(
    private val credentialRepository: CredentialRepository
) : ViewModel() {

    // The UI layer only need to know if the operation has been finished,
    // MutableLiveData is not necessary
    fun editCredential(credential: Credential): LiveData<CredentialUiState> = liveData {
        emit(CredentialUiState.Loading)
        CredentialHelper.filterCredential(credential)
        credentialRepository.editCredential(credential)
        emit(CredentialUiState.Success)
    }

    // The UI layer only need to know if the operation has been finished,
    // MutableLiveData is not necessary
    fun deleteCredentialById(id: String): LiveData<CredentialUiState> = liveData {
        emit(CredentialUiState.Loading)
        credentialRepository.deleteCredentialById(id)
        emit(CredentialUiState.Success)
    }
}

sealed interface CredentialUiState {
    object Loading : CredentialUiState
    object Success : CredentialUiState
}