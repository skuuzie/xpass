package com.skuuzie.xpass.ui.credential

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.skuuzie.xpass.data.local.CredentialRepository
import com.skuuzie.xpass.data.local.database.Credential
import com.skuuzie.xpass.util.CredentialHelper
import com.skuuzie.xpass.util.wrapEspressoIdlingResource
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

        wrapEspressoIdlingResource {
            try {
                CredentialHelper.filterCredential(credential)
                credentialRepository.editCredential(credential)
                emit(CredentialUiState.Success)
            } catch (e: Exception) {
                emit(CredentialUiState.Error(e.message.toString()))
            }
        }
    }

    // The UI layer only need to know if the operation has been finished,
    // MutableLiveData is not necessary
    fun deleteCredentialById(id: String): LiveData<CredentialUiState> = liveData {
        emit(CredentialUiState.Loading)

        wrapEspressoIdlingResource {
            try {
                credentialRepository.deleteCredentialById(id)
                emit(CredentialUiState.Success)
            } catch (e: Exception) {
                emit(CredentialUiState.Error(e.message.toString()))
            }
        }
    }
}

sealed interface CredentialUiState {
    data class Error(val message: String) : CredentialUiState
    object Loading : CredentialUiState
    object Success : CredentialUiState
}