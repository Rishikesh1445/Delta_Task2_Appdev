package com.example.cheesechase

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun FrontPage(navController: NavController){

    Button(onClick = { navController.navigate(Screen.gamePage.route) }) {

    }
}