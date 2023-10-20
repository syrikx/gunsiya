package com.hyunakim.gunsiya.ui.home

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hyunakim.gunsiya.data.User
import com.hyunakim.gunsiya.ui.AppViewModelProvider
import com.hyunakim.gunsiya.ui.user.UserSelect

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
//    navigateToItemUpdate :(Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory= AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    Text("${viewModel.currentUser.collectAsState().value.name}님 환영합니다.")
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