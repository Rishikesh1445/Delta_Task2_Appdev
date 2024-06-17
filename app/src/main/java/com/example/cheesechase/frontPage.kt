package com.example.cheesechase

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FrontPage(navController: NavController , dimension: WindowInfo){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.hsv(359f, 0.84f, 0.49f))
    )

    val circleOneRadius = remember{Animatable(0f) }
    val circleTwoRadius = remember{Animatable(0f) }
    val circleThreeRadius = remember{Animatable(0f) }
    val imageSize = remember{ Animatable(0f) }

    LaunchedEffect(Unit) {
        launch{circleOneRadius.animateTo(dimension.screenWidth / 2 + 150f, tween(600 , easing = EaseInOutBounce))}
        launch{circleTwoRadius.animateTo(dimension.screenWidth / 2 - 50f, tween(600 , easing = EaseInOutBounce))}
        launch { circleThreeRadius.animateTo(dimension.screenWidth / 2 - 200f, tween(600 , easing = EaseInOutBounce));
            imageSize.animateTo(300f, tween(600, easing = EaseInOutBounce))}
    }

    Canvas(modifier= Modifier.fillMaxSize()){
        drawCircle(
            color = Color.hsv(358f, 0.81f, 0.65f),
            radius = circleOneRadius.value
        )
        drawCircle(
            color= Color.hsv(358f, 0.81f, 0.9f),
            radius = circleTwoRadius.value
        )
        drawCircle(
            color = Color.hsv(22f, 0.7f, 1f),
            radius = circleThreeRadius.value
        )
    }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.frontpage), contentDescription = null,
            modifier = Modifier.size(imageSize.value.dp)
        )
    }
    Button(onClick = { navController.navigate(Screen.gamePage.route) }){}
}