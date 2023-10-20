package com.hyunakim.gunsiya.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.data.UsersRepository
import com.hyunakim.gunsiya.ui.user.toUser
import com.hyunakim.gunsiya.ui.user.toUserUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val usersRepository: UsersRepository) : ViewModel(){

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
//    private val _currentUser = MutableStateFlow(User(
//        id = 0,
//        name = "",
//        birthDate = "",
//        hospitalCode = "",
//        patientCode = "",
//        isCurrentUser = true
//    ))
//    val currentUser: StateFlow<User> = _currentUser
    private val _currentUser = MutableStateFlow(User(
        id = 0,
        name = "",
        birthDate = "",
        hospitalCode = "",
        patientCode = "",
        isCurrentUser = true
    ))
    val currentUser: StateFlow<User> = _currentUser
    init {
        viewModelScope.launch {
            updateCurrentUser(getUser(8))
        }
    }
    fun updateCurrentUser(user:User){
        _currentUser.value = user
    }
    suspend fun getUser(userId : Int) : User{
        val user = usersRepository.getUserStream(userId)
            .filterNotNull().first()
            .toUserUiState(true)
        return user.userDetails.toUser()
    }
}


