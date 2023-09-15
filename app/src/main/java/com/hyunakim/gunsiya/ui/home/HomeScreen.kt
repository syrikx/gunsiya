package com.hyunakim.gunsiya.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.ui.AppViewModelProvider

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
//    navigateToItemUpdate :(Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory= AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    HomeBody(
        userList = homeUiState.userList,
//        onItemClick = navigateToItemUpdate,
        modifier = Modifier
            .fillMaxSize()
    )
}

@Composable
fun HomeBody(userList: List<User>,
//             onItemClick: (Int) -> Unit,
             modifier: Modifier = Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (userList.isEmpty()) {
            Text(
                text = "등록된 유저가 없습니다.",
                textAlign = TextAlign.Center
            )
        } else {
            UserSelect(userList)
        }
    }
}

@Composable
fun UserSelect(){

}

@Composable
fun UserSelect(
    userList : List<User>,
    showAddUserButton : Boolean = false
){
    Row {
        userList.forEach{ it ->
            Button(onClick = { /*TODO*/ }) {
                Text(it.name)
            }
        }
        if (showAddUserButton){
            Button(onClick = { /*TODO*/ }) {
                Text("+")
            }
        }
    }
}