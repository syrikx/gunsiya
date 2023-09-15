package com.hyunakim.gunsiya.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyunakim.gunsiya.R
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.ui.AppViewModelProvider
import com.hyunakim.gunsiya.ui.home.UserSelect
import kotlinx.coroutines.launch

@Composable
fun UserScreen(
    modifier: Modifier = Modifier,
    viewModel: UserEntryViewModel = viewModel(factory= AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        UserSelect(userList = homeUiState.userList, showAddUserButton = true)
        UserEntryScreen(
            navigateBack = { /*TODO*/ },
            onNavigateUp = { /*TODO*/ },
            viewModel = viewModel,
            modifier = modifier
        )
    }
}

//@Composable
//fun UserScreen(
//    modifier: Modifier = Modifier,
//    viewModel: UserEntryViewModel = viewModel(factory= AppViewModelProvider.Factory)
//) {
//    Column {
//        val userList: List<User> = listOf(
//            User(
//                id = 1,
//                name = "홍길동",
//                birthDate = "1990-01-01",
//                hospitalCode = "H001",
//                patientCode = "P001"
//            ),
//            User(
//                id = 2,
//                name = "이순신",
//                birthDate = "1980-12-31",
//                hospitalCode = "H002",
//                patientCode = "P002"
//            )
//        )
//        Text(text = "User")
//        UserSelect(userList)
//        UserInput(viewModel.userUiState)
//    }
//}

@Composable
fun UserEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: UserEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        UserEntryBody(
            userUiState = viewModel.userUiState,
            onUserValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveUser()
                    navigateBack()
                }
            },
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun UserEntryBody(
    userUiState: UserUiState,
    onUserValueChange: (UserDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
//        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        UserInput(
            userDetails = userUiState.userDetails,
            onValueChange = onUserValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "취소")
        }
        Button(
            onClick = onSaveClick,
            enabled = userUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "저장")
        }
    }
}


@Composable
fun UserInput(
//    userUiState: UserUiState,
    userDetails: UserDetails,
    modifier: Modifier = Modifier,
    onValueChange: (UserDetails) -> Unit = {},
    enabled: Boolean = true
){
//    val userDetails = remember { mutableStateOf(userUiState.userDetails) }
//    val userDetails = userUiState.userDetails
//    val name = remember { mutableStateOf(userDetails.name) }
//    val birthDate = remember { mutableStateOf(userDetails.birthDate) }
//    val hospitalCode = remember { mutableStateOf(userDetails.hospitalCode) }
//    val patientCode = remember { mutableStateOf(userDetails.patientCode) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = userDetails.name,
            onValueChange = { onValueChange(userDetails.copy(name = it)) },
            label = { Text("이름") }
        )
        Spacer(modifier=Modifier.height(16.dp))
        OutlinedTextField(
            value = userDetails.birthDate,
            onValueChange = { onValueChange(userDetails.copy(birthDate = it)) },
            label={Text("생년월일")}
        )
        Spacer(modifier=Modifier.height(16.dp))
        OutlinedTextField(
            value=userDetails.hospitalCode,
            onValueChange={ onValueChange(userDetails.copy(hospitalCode = it))},
            label={Text("병원코드")}
        )
        Spacer(modifier=Modifier.height(16.dp))
        OutlinedTextField(
            value=userDetails.patientCode,
            onValueChange={onValueChange(userDetails.copy(patientCode = it))},
            label={Text("환자코드")}
        )
        Spacer(modifier=Modifier.height(24.dp))
//        Button(onClick={
//            // TODO: 여기에 제출 버튼 클릭 시 수행할 동작을 구현하세요.
//            // 예: 데이터베이스에 사용자 정보 저장하기
//        }) {
//            Text(text="저장")
//        }
    }
}