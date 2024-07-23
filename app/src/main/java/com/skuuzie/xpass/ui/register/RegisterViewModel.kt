package com.skuuzie.xpass.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skuuzie.xpass.data.datastore.DatastoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val datastoreManager: DatastoreManager
) : ViewModel() {

    // MutableLiveData is ideal for continuous monitoring of a certain state
    private val _registerState = MutableLiveData<RegisterUiState>()
    val registerState: LiveData<RegisterUiState> get() = _registerState

    suspend fun registerUser(username: String, password: String) {
        _registerState.value = RegisterUiState.Loading
        datastoreManager.updateUserName(username)
        datastoreManager.updateUserPassword(password)
        _registerState.value = RegisterUiState.Success
    }
}

sealed interface RegisterUiState {
    object Loading : RegisterUiState
    object Error : RegisterUiState
    object Success : RegisterUiState
}