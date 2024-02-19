package com.hyunakim.gunsiya

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hyunakim.gunsiya.ui.MainViewModel
import com.hyunakim.gunsiya.ui.components.GunsiyaTabRow
import com.hyunakim.gunsiya.ui.home.HomeScreen
import com.hyunakim.gunsiya.ui.qna.QnaScreen
import com.hyunakim.gunsiya.ui.result.ResultScreen
import com.hyunakim.gunsiya.ui.theme.GunsiyaTheme
import com.hyunakim.gunsiya.ui.user.UserScreen

val LocalAlarmManagerHelper = compositionLocalOf<AlarmManagerHelper> {
    error("No AlarmManagerHelper provided")
}


@Composable
fun GunsiyaApp(
//    alarmManagerHelper: AlarmManagerHelper,
    navController: NavHostController = rememberNavController()
){
    val context = LocalContext.current
    val appContext = LocalContext.current.applicationContext as GunsiyaApplication
    val appContainer = appContext.container
    val usersRepository = appContainer.usersRepository
    val alarmManagerHelper = remember { AlarmManagerHelper(context) }
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(alarmManagerHelper, usersRepository))
//    val mainViewModel: MainViewModel = viewModel(
//        factory = MainViewModelFactory(alarmManagerHelper)
//    )
    CompositionLocalProvider(LocalAlarmManagerHelper provides alarmManagerHelper) {
        GunsiyaTheme {
//        val backStackEntry by navController.currentBackStackEntryAsState()
//        val currentScreen = GunsiyaScreen.valueOf(
//            backStackEntry?.destination?.route ?: GunsiyaScreen.Home.name
//        )
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStack?.destination
            val currentScreen = gunsiyaTabRowScreens.find { it.route == currentDestination?.route }
                ?: HomeDestination
            Scaffold(
//            backgroundColor = MaterialTheme.colorScheme.background,
                topBar = {
                    GunsiyaTabRow(
                        allScreens = gunsiyaTabRowScreens,
//                    onTabSelected = { screen -> currentScreen = screen },
                        onTabSelected = { newScreen -> navController.navigateSingleTopTo(newScreen.route) },
                        currentScreen = currentScreen
                    )
                }
            ) { innerPadding ->
                GunsiyaNavHost(
                    mainViewModel = mainViewModel,
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GunsiyaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    val homeUiState by mainViewModel.homeUiState.collectAsState()
    val usersEmpty = remember { mutableStateOf(homeUiState.userList.isEmpty()) }
    val usersLoaded = remember { mutableStateOf(false) }

    Log.d("usersEmpty :", usersEmpty.toString())
    val context = LocalContext.current

    LaunchedEffect(key1 = homeUiState) {
        usersEmpty.value = homeUiState.userList.isEmpty()
        Log.d("LaunchedEffect :", homeUiState.userList.toString())
        Log.d("LaunchedEffect :", usersEmpty.toString())
        usersLoaded.value = true
    }

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = Routes.Home.route) {
                HomeScreen(mainViewModel = mainViewModel)
        }
        composable(route = Routes.Result.route) {
            if (usersEmpty.value && usersLoaded.value) {
                redirectToUserScreen(navController, context)
            } else {
                ResultScreen(mainViewModel = mainViewModel)
            }
        }
        composable(route = Routes.Qna.route) {
            if (usersEmpty.value && usersLoaded.value) {
                redirectToUserScreen(navController, context)
            } else {
                QnaScreen()
            }
        }
        composable(route = Routes.User.route) {
            UserScreen(mainViewModel = mainViewModel)
        }
    }
}

@Composable
private fun redirectToUserScreen(navController: NavHostController, context: Context) {
    LaunchedEffect(Unit) {
        Toast.makeText(context, "등록된 사용자가 없어 사용자 등록 화면으로 자동 이동합니다", Toast.LENGTH_LONG).show()
        navController.navigate(Routes.User.route) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route:String) =
    this.navigate(route){
//        popUpTo(
//            this@navigateSingleTopTo.graph.findStartDestination().id
//        ) {
//            saveState = true
//        }
        launchSingleTop = true
//        restoreState = true
    }