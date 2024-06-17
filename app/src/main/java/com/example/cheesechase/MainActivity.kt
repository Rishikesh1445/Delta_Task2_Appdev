package com.example.cheesechase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cheesechase.ui.theme.CheeseChaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheeseChaseTheme {
                val context = LocalContext.current
                val viewModel= GameViewModel()
                val dimension = Dimensions()
                val gyroscope = Gyro(context)
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.frontPage.route){
                    composable(route = Screen.frontPage.route){
                        FrontPage(navController, dimension)
                    }
                    composable(route = Screen.gamePage.route){
                        GamePage(navController,viewModel,context, dimension, gyroscope)
                    }
                }
            }
        }
    }
}