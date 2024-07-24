package com.skuuzie.xpass.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skuuzie.xpass.data.datastore.DatastoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val datastoreManager: DatastoreManager
) : ViewModel() {

    // MutableLiveData is ideal for continuous monitoring of a certain state
    private val _loginState = MutableLiveData<LoginUiState>()
    val loginState: LiveData<LoginUiState> get() = _loginState

    fun loadUserPassword(password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            if (datastoreManager.loadUserPassword(password)) {
                _loginState.value = LoginUiState.Success
            } else {
                _loginState.value = LoginUiState.Error
            }
        }
    }
}

sealed interface LoginUiState {
    object Loading : LoginUiState
    object Error : LoginUiState
    object Success : LoginUiState
}