package com.report.kurs

sealed class Route( val route: String ) {
    object Home: Route("home")
    object Game: Route("game")
    object History: Route("history")
    object Setting: Route("setting")
}