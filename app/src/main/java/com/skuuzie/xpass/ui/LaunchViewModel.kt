package com.skuuzie.xpass.ui

import androidx.lifecycle.ViewModel
import com.skuuzie.xpass.data.datastore.DatastoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val datastoreManager: DatastoreManager
) : ViewModel() {
    fun initializeDatastore(): Flow<LaunchActivityInitState> = flow {
        emit(LaunchActivityInitState.Loading)
        emit(
            LaunchActivityInitState.Finished(
                datastoreManager.currentUserUsername.first().isNotEmpty()
            )
        )
    }
}

sealed interface LaunchActivityInitState {
    data object Loading : LaunchActivityInitState
    data class Finished(val isRegistered: Boolean) : LaunchActivityInitState
}