package com.hyunakim.gunsiya.ui.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.data.UsersRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserEntryViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
//    private val userId: Int = 1
    data class AllUsersState(val userList: List<User> = listOf())
    val allUsersState : StateFlow<AllUsersState> = usersRepository.getAllUsersStream().map { it -> AllUsersState(it)}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = AllUsersState()
        )
    var userUiState by mutableStateOf(UserUiState())
        private set
//    init {
//        viewModelScope.launch {
//            getUser(userId)
//        }
//    }
//    val uiState : StateFlow<UserUiState> =
//        usersRepository.getUserStream(userId)
//            .filterNotNull()
//            .map { it ->
//                    UserUiState(userDetails = it.toUserDetails())}
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//            initialValue = UserUiState()
//        )


    /**
     * Holds current user ui state
     */


    /**
     * Updates the [userUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(userDetails: UserDetails) {
        userUiState =
            UserUiState(userDetails = userDetails, isEntryValid = validateInput(userDetails))
    }

    private fun validateInput(uiState: UserDetails = userUiState.userDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && birthDate.isNotBlank() && hospitalCode.isNotBlank() && patientCode.isNotBlank()
        }
    }

    suspend fun saveUser(){
        if (validateInput()){
            usersRepository.insertUser(userUiState.userDetails.toUser())
        }
    }

    suspend fun getUser(userId : Int){
        userUiState = usersRepository.getUserStream(userId)
            .filterNotNull().first()
            .toUserUiState(true)
    }
    fun initUser(){
        userUiState = UserUiState()
    }
    suspend fun deleteUser(user : User){
        usersRepository.deleteUser(user)
    }
}

/**
 * Represents Ui State for an User.
 */
data class UserUiState(
    val userDetails: UserDetails = UserDetails(),
    val isEntryValid: Boolean = false
)

data class UserDetails(
    val id: Int = 0,
    val name: String = "",
    val birthDate: String = "",
    val hospitalCode: String = "",
    val patientCode: String = "",
    val isCurrentUser : Boolean = false,
)

/**
 * Extension function to convert [UserDetails] to [User]. If the value of [UserDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [UserDetails.quantity] is not a valid [Int], then the quantity will be set to 0
 */
fun UserDetails.toUser(): User = User(
    id = id,
    name = name,
    birthDate = birthDate,
    hospitalCode = hospitalCode,
    patientCode = patientCode,
    isCurrentUser = isCurrentUser
)
/**
 * Extension function to convert [User] to [UserUiState]
 */
fun User.toUserUiState(isEntryValid: Boolean = false): UserUiState = UserUiState(
    userDetails = this.toUserDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [User] to [UserDetails]
 */
fun User.toUserDetails(): UserDetails = UserDetails(
    id = id,
    name = name,
    birthDate = birthDate,
    hospitalCode = hospitalCode,
    patientCode = patientCode,
    isCurrentUser = isCurrentUser
)


