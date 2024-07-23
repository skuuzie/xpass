package com.skuuzie.xpass.ui

import androidx.lifecycle.ViewModel
import com.skuuzie.xpass.data.datastore.DatastoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val datastoreManager: DatastoreManager
) : ViewModel() {
    val initState: Flow<MainActivityInitState> = datastoreManager.currentUserUsername.map {
        MainActivityInitState.Finished(it.isNotEmpty())
    }
}

sealed interface MainActivityInitState {
    data object Loading : MainActivityInitState
    data class Finished(val isRegistered: Boolean) : MainActivityInitState
}