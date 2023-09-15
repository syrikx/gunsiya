package com.hyunakim.gunsiya

enum class GunsiyaScreen(val route : String) {
    Home("home"),
    User("user"),
    Result("result"),
    Qna("qna")
}

enum class Routes(val route : String) {
    Home("home"),
    User("user"),
    Result("result"),
    Qna("qna")
}

interface GunsiyaDestination {
//    val icon: ImageVector
    val route: String
}

/**
 * Rally app navigation destinations
 */
object HomeDestination : GunsiyaDestination {
//    override val icon = Icons.Filled.PieChart
    override val route = "home"
}

object UserDestination : GunsiyaDestination {
//    override val icon = Icons.Filled.AttachMoney
    override val route = "user"
}

object ResultDestination : GunsiyaDestination {
//    override val icon = Icons.Filled.MoneyOff
    override val route = "result"
}

object QnaDesitination : GunsiyaDestination {
    //    override val icon = Icons.Filled.MoneyOff
    override val route = "qna"
}

//val gunsiyaTabRowScreens = listOf(GunsiyaScreen.Home, GunsiyaScreen.User, GunsiyaScreen.Result, GunsiyaScreen.Qna)
val gunsiyaTabRowScreens = listOf(HomeDestination, UserDestination, ResultDestination, QnaDesitination)

