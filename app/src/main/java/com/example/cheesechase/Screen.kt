package com.example.cheesechase

sealed class Screen(val route:String) {
    object frontPage: Screen("frontPage")
    object gamePage: Screen("gamePage")
}