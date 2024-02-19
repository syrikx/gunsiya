package com.hyunakim.gunsiya.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyunakim.gunsiya.data.Record
import com.hyunakim.gunsiya.data.RecordsRepository
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.data.UsersRepository
import com.hyunakim.gunsiya.data.apiService
import com.hyunakim.gunsiya.data.convertToUserWithRecord
import com.hyunakim.gunsiya.ui.user.toUser
import com.hyunakim.gunsiya.ui.user.toUserUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

class HomeViewModel(private val usersRepository: UsersRepository, private val recordsRepository: RecordsRepository) : ViewModel(){

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
    private val _currentUser = MutableStateFlow(User(
        id = 0,
        name = "",
        birthDate = "",
        hospitalCode = "",
        patientCode = "",
        lastSelectedTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    ))
    val currentUser = _currentUser.asStateFlow()
    init {
//        GunsiyaApplication.UserManager.initializeWithMostRecentUser(usersRepository)
        viewModelScope.launch {
            homeUiState.collect { homeUiState ->
                val mostRecentUser = homeUiState.userList.maxByOrNull { it.lastSelectedTime }
                if (mostRecentUser != null) {
                    updateCurrentUser(mostRecentUser)
                }
            }
        }
    }
    suspend fun updateCurrentUser(user:User){
        Log.d("updateCurrentUser", user.toString())
        _currentUser.value = user
        getUserRecords(user.id)
    }
    suspend fun getUser(userId : Int) : User{
        val user = usersRepository.getUserStream(userId)
            .filterNotNull().first()
            .toUserUiState(true)
        return user.userDetails.toUser()
    }
    private val _selectedDay = MutableStateFlow(Clock.System.todayIn(
        TimeZone.currentSystemDefault()
    ))
    val selectedDay = _selectedDay.asStateFlow()
    fun updateSelectedDay(date:LocalDate){
        _selectedDay.value = date
//        updateSelectedRecord(getRecord(currentUser.value.id,selectedDay.toString()))
    }
    data class RecordsState(val recordList: List<Record> = listOf())
//    val recordsState :MutableStateFlow<RecordsState> = recordsRepository.getRecordsByUserId(currentUser.value.id).map {RecordsState(it)}
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//            initialValue = RecordsState()
//        )
    val recordsState = MutableStateFlow(RecordsState())

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    private val _selectedRecord = MutableStateFlow(
        com.hyunakim.gunsiya.data.Record(
            userId = 0,
            date = "",
            isAtropineDrop = false,
            timeOutdoorActivity = 0.0,
            timeCloseWork = 0.0,
        )
    )

    //    val currentUser: StateFlow<User> = _currentUser
    val selectedRecord = _selectedRecord.asStateFlow()
    init {
        viewModelScope.launch {
            updateSelectedRecord(getRecord(currentUser.value.id,selectedDay.toString()))
        }
    }
    fun updateSelectedRecord(record: Record) {
        if (_selectedRecord.value != record) {
            _selectedRecord.value = record
            _hasChanges.value = true
        }
    }

    fun resetChangesFlag() {
        _hasChanges.value = false
    }

    fun getSelectedRecord(recordsState: RecordsState, selectedDate : LocalDate){
        var temp = recordsState.recordList.firstOrNull { LocalDate.parse(it.date) == selectedDate }
        if (temp != null) {
            _selectedRecord.value = temp
        } else {
            val initSlectedRecord = com.hyunakim.gunsiya.data.Record(
                userId =selectedRecord.value.userId,
                date = selectedDate.toString(),
                isAtropineDrop = false,
                timeOutdoorActivity = 0.0,
                timeCloseWork = 0.0,
            )
            _selectedRecord.value = initSlectedRecord
        }
    }
    suspend fun getUserRecords(userId : Int) : List<Record>{
        val records = recordsRepository.getRecordsByUserId(userId)
            .filterNotNull().first()
//            .toUserUiState(true)
//        return user.userDetails.toUser()
        recordsState.value = RecordsState(records)
        return records
    }
    suspend fun getRecord(userId : Int, date : String) : Record{
        val record = recordsRepository.getRecordsByUserIdAndDate(userId, date)
            .filterNotNull().first()
//            .toUserUiState(true)
//        return user.userDetails.toUser()
        return record
    }

    suspend fun saveRecord(){
        Log.d("saveRecord", "${selectedRecord.value}, ")
        recordsRepository.insertRecord(selectedRecord.value)
        getUserRecords(selectedRecord.value.userId)
    }
    private val _uploadStatus = MutableStateFlow<String?>(null)
    val uploadStatus: StateFlow<String?> = _uploadStatus.asStateFlow()
    suspend fun uploadUsers() {
        val users = homeUiState.value.userList
        var success = true
        var message = "업로드 성공"

        for (user in users) {
            try {
                val records = getUserRecords(user.id)
                val userWithRecord = convertToUserWithRecord(user, records)
                val response = apiService.sendUserWithRecord(userWithRecord)
                Log.d("uploadUsers response", response.toString())
                if (!response.success) {
                    success = false
                    message = response.message // 서버에서 반환된 실패 메시지
                    break
                }
            } catch (e: Exception) {
                success = false
                message = "업로드 실패: ${e.message}"
                break
            }
        }

        if (!success) {
            // 업로드가 실패한 경우
            _uploadStatus.value = message
            Log.d("uploadUsers Fail", "Fail")
        } else {
            // 모든 업로드가 성공한 경우
            _uploadStatus.value = "모든 사용자 데이터가 성공적으로 업로드되었습니다."
            Log.d("uploadUsers Success", "Success")
        }
        resetChangesFlag()
    }
    fun resetUploadStatus() {
        _uploadStatus.value = null
    }
}


