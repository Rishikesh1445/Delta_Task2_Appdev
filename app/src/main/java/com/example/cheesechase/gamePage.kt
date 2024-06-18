package com.example.cheesechase

import android.content.Context
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GamePage(navController: NavController, viewModel: GameViewModel, context: Context, dimension: WindowInfo, gyroscope: Gyroscope){
    val state = viewModel.state.value

    //GyroScope
    if (state.gyroMode) { viewModel.startGame(gyroscope, dimension)}

    //Well, just an dramatic entrance for tom and jerry from screen bottom
    viewModel.openingAnimation(dimension)

    //White Tracks in Road and Animation
    trackAnimation(dimension, { x -> viewModel.trackClicked(x, dimension) }, {viewModel.speedReset()})

    //here, 3 Obstacles are added for each track, if needed more, simply create another function
    Obstacle_Middle(dimension , {track -> viewModel.delay(track)},{ track-> viewModel.obstacleCrossed(track , context) } , { track -> viewModel.tomJump(track)},{viewModel.speedReset()})
    Obstacle_Middle(dimension , {track ->viewModel.delay(track)},{ track-> viewModel.obstacleCrossed(track , context) } , { track -> viewModel.tomJump(track)},{viewModel.speedReset()})
    Obstacle_Middle(dimension , {track -> viewModel.delay(track)},{ track-> viewModel.obstacleCrossed(track , context) } , { track -> viewModel.tomJump(track)},{viewModel.speedReset()})

    Obstacle_Left(dimension , {track -> viewModel.delay(track)},{ track-> viewModel.obstacleCrossed(track , context) } , { track -> viewModel.tomJump(track)},{viewModel.speedReset()})
    Obstacle_Left(dimension , {track -> viewModel.delay(track)},{ track-> viewModel.obstacleCrossed(track , context) } , { track -> viewModel.tomJump(track)}, {viewModel.speedReset()})
    Obstacle_Left(dimension , {track -> viewModel.delay(track)},{ track-> viewModel.obstacleCrossed(track , context) } , { track -> viewModel.tomJump(track)},  {viewModel.speedReset()})

    Obstacle_Right(dimension , {track -> viewModel.delay(track)},{ track-> viewModel.obstacleCrossed(track , context) } , { track -> viewModel.tomJump(track)}, {viewModel.speedReset()})
    Obstacle_Right(dimension , {track -> viewModel.delay(track)},{ track-> viewModel.obstacleCrossed(track , context) } , { track -> viewModel.tomJump(track)}, {viewModel.speedReset()})
    Obstacle_Right(dimension , {track -> viewModel.delay(track)},{ track-> viewModel.obstacleCrossed(track , context) } , { track -> viewModel.tomJump(track)}, {viewModel.speedReset()})

    Heart(state.heart, { viewModel.sendgamePause() }, dimension, { track ->viewModel.delay(track)} , { track-> viewModel.heartJerryBool(track)})
    //Trap(dimension, {track ->viewModel.delay(track)} , {track-> viewModel.trapJerryBool(track)})
    //Cheese(dimension, {track ->viewModel.delay(track)} , {track-> viewModel.cheeseJerryBool(track)})


    //Scores in Top Bar
    Score(state.highscore, dimension)
    CheeseScore(state.cheeseScore)
    HeartTime(state.heartTime, dimension)

    //determines the y position of Tom based on hits
    viewModel.closeToJerry(dimension)
    viewModel.HeartJerryTime()
    viewModel.trapRandom()
    viewModel.CheeseCount()

    //Jerry Face Animation X axis
    val jerryTranslationx by animateDpAsState(
        targetValue = state.jerryPositionx,
        label = "",
        animationSpec = tween(durationMillis = 200 , easing = LinearOutSlowInEasing)
    )

    //BELOW PROPERTY BELONGS TO TOM AND JERRY
    //Jerry Face Animation Y axis
    val jerryTranslationy by animateDpAsState(
        targetValue = state.jerryPositiony,
        label = "",
        animationSpec = tween(durationMillis = 2000 , easing = LinearOutSlowInEasing)
    )

    //Jerry Jump Animation
    if(state.jerryJump){viewModel.JerryJumping(context)}
    val jerrySize by animateDpAsState(
        targetValue = state.jerrySize,
        label = "",
        animationSpec = tween(durationMillis = 200 , easing = LinearOutSlowInEasing)
    )

    //Jerry Face
    Image(painter = painterResource(id = R.drawable.jerry_face),
        contentDescription = null,
        modifier = Modifier
            .size(jerrySize, jerrySize)
            .offset(jerryTranslationx, jerryTranslationy)
            .clickable { viewModel.jumpJerry() }
    )

    //Tom Face Animation X axis
    val tomTranslationx by animateDpAsState(
        targetValue = state.tomPositionx,
        label = "",
        animationSpec = tween(durationMillis = 200 , easing = LinearOutSlowInEasing)
    )

    //Tom Face Animation Y axis
    val tomTranslationy by animateDpAsState(
        targetValue = state.tomPositiony,
        label = "",
        animationSpec = tween(durationMillis = state.tomPositionyTiming, easing = LinearOutSlowInEasing)
    )

    //Tom Jump Animation
    if(state.tomJump){viewModel.TomJumping()}
    val tomSize by animateDpAsState(
        targetValue = state.tomSize,
        label = "",
        animationSpec = tween(durationMillis = 200 , easing = LinearOutSlowInEasing)
    )

    //Tom Face
    Image(painter = painterResource(id = R.drawable.tom_face),
        contentDescription = null,
        modifier = Modifier
            .size(tomSize, tomSize)
            .offset(tomTranslationx, tomTranslationy)
    )

    viewModel.gameOverFunction()
    if(state.gameOver){gameover({},{},{navController.navigate(Screen.frontPage.route)})}

    Row(modifier = Modifier.fillMaxWidth().padding(8.dp).offset(0.dp, dimension.screenHeightinDp - 75.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween , verticalAlignment = Alignment.CenterVertically){
        Button(onClick = { viewModel.gyrobutton() }) {
            Icon(painter = painterResource(id = R.drawable.baseline_screen_rotation_24), contentDescription = null,
                modifier = Modifier.size(20.dp))
        }
        Button(onClick = { viewModel.gamePause() }) {
            Icon(painter = painterResource(id = R.drawable.baseline_pause_24), contentDescription = null,
                modifier = Modifier.size(20.dp))
        }
    }
}