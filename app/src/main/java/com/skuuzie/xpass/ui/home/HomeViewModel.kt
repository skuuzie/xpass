package com.skuuzie.xpass.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.skuuzie.xpass.data.local.CredentialRepository
import com.skuuzie.xpass.data.local.database.Credential
import com.skuuzie.xpass.util.EspressoIdlingResource
import com.skuuzie.xpass.util.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val credentialRepository: CredentialRepository
) : ViewModel() {

    // Data source is a local Room database, better use flow
    fun getCredentials(): Flow<HomeUiState> = flow {
        emit(HomeUiState.Loading)

        EspressoIdlingResource.increment()

        credentialRepository.mCredentials.collect { credentials ->
            try {
                emit(HomeUiState.Success(credentials))
                EspressoIdlingResource.decrement()
            } catch (e: Exception) {
                emit(HomeUiState.Error(e.message.toString()))
                EspressoIdlingResource.decrement()
            }
        }
    }

    // The UI layer only need to know if the operation has been finished,
    // MutableLiveData is not necessary
    fun addNewCredential(): LiveData<HomeUiState> = liveData {
        emit(HomeUiState.Loading)

        wrapEspressoIdlingResource {
            try {
                val cred = Credential(
                    uuid = UUID.randomUUID().toString(),
                    platform = "New Credential",
                    username = "-",
                    email = "-",
                    password = "-"
                )
                val clearCred = cred.copy()

                credentialRepository.addCredential(cred)
                emit(HomeUiState.Success(listOf(clearCred)))
            } catch (e: Exception) {
                emit(HomeUiState.Error(e.message.toString()))
            }
        }
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Success(val data: List<Credential>) : HomeUiState
}