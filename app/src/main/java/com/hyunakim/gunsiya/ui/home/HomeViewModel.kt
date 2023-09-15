package com.hyunakim.gunsiya.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.data.UsersRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(usersRepository: UsersRepository) : ViewModel(){

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    data class HomeUiState(val userList: List<User> = listOf())
    val homeUiState :StateFlow<HomeUiState> = usersRepository.getAllUsersStream().map {HomeUiState(it)}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )
}


