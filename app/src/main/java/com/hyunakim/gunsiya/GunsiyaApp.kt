package com.hyunakim.gunsiya

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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


@Composable
fun GunsiyaApp(
    alarmManagerHelper: AlarmManagerHelper,
    navController: NavHostController = rememberNavController()
){
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(alarmManagerHelper)
    )
    GunsiyaTheme {
//        val backStackEntry by navController.currentBackStackEntryAsState()
//        val currentScreen = GunsiyaScreen.valueOf(
//            backStackEntry?.destination?.route ?: GunsiyaScreen.Home.name
//        )
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen = gunsiyaTabRowScreens.find { it.route == currentDestination?.route } ?:HomeDestination
        Scaffold(
//            backgroundColor = MaterialTheme.colorScheme.background,
            topBar = {
                GunsiyaTabRow(
                    allScreens = gunsiyaTabRowScreens,
//                    onTabSelected = { screen -> currentScreen = screen },
                    onTabSelected = {newScreen -> navController.navigateSingleTopTo(newScreen.route)},
                    currentScreen = currentScreen
                )
            }
        ){ innerPadding ->
            GunsiyaNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
        }
    }
}
@Composable
fun GunsiyaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    NavHost(
        navController=navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ){
        composable(route = Routes.Home.route){
            HomeScreen(
            )
        }
        composable(route = Routes.Result.route){
            ResultScreen(
            )
        }
        composable(route = Routes.Qna.route){
            QnaScreen()
        }
        composable(route = Routes.User.route){
            UserScreen()
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