package com.example.cheesechase

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInOutBounce
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FrontPage(viewModel: GameViewModel, context: Context, navController: NavController, dimension: WindowInfo) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.hsv(359f, 0.84f, 0.49f))
    )

    val circleOneRadius = remember { Animatable(0f) }
    val circleTwoRadius = remember { Animatable(0f) }
    val circleThreeRadius = remember { Animatable(0f) }
    val circleThreeColor = remember {
        androidx.compose.animation.Animatable(
            Color.hsv(
                22f,
                0.7f,
                1f
            )
        )
    }
    val imageSize = remember { Animatable(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "")
    var boolText by remember { mutableStateOf(false) }
    var boolClicked by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.bg(context)
        launch {
            circleOneRadius.animateTo(
                dimension.screenWidth / 2 + 150f,
                tween(600, easing = EaseInOutBounce)
            )
        }
        launch {
            circleTwoRadius.animateTo(
                dimension.screenWidth / 2 - 50f,
                tween(600, easing = EaseInOutBounce)
            )
        }
        launch {
            circleThreeRadius.animateTo(
                dimension.screenWidth / 2 - 200f,
                tween(600, easing = EaseInOutBounce)
            );
            imageSize.animateTo(300f, tween(600, easing = EaseInOutBounce)); boolText = true
        }
    }
    if (boolClicked) {
        LaunchedEffect(Unit) {
            launch {
                imageSize.animateTo(0f, tween(0, easing = EaseInElastic));
                circleOneRadius.animateTo(
                    dimension.screenWidth / 2 + 350f,
                    tween(600, easing = EaseInElastic)
                )
            }
            launch {
                circleTwoRadius.animateTo(
                    dimension.screenWidth / 2 + 150f,
                    tween(600, easing = EaseInElastic)
                );
                circleThreeColor.animateTo(Color.hsv(185f, 0.81f, 1f))
            }
            launch {
                boolText = false;circleThreeRadius.animateTo(
                dimension.screenWidth / 2 + 800f,
                tween(600, easing = EaseInElastic)
            );
                delay(1000);navController.navigate(Screen.gamePage.route)
            }
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .clickable { boolClicked = true }) {
        drawCircle(
            color = Color.hsv(358f, 0.81f, 0.65f),
            radius = circleOneRadius.value
        )
        drawCircle(
            color = Color.hsv(358f, 0.81f, 0.9f),
            radius = circleTwoRadius.value
        )
        drawCircle(
            color = circleThreeColor.value,
            radius = circleThreeRadius.value
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.frontpage), contentDescription = null,
            modifier = Modifier.size(imageSize.value.dp)
        )
        if (boolText) {
            val textAlpha by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 400, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )
            Text(
                "TAP ANYWHERE TO BEGIN",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(textAlpha)
            )
        }
    }
}

@Composable
fun trackAnimation(
    paused: () -> Boolean,
    dimension: WindowInfo,
    whereClicked: (Float) -> Unit,
    speedReset: () -> Boolean
) {
    //Animation
    val translate = remember { Animatable(-3 * dimension.screenHeight / 20) }
    var time = 1500
    LaunchedEffect(Unit) {
        fun animation() {
            launch {
                while (!paused()) {
                    translate.animateTo(
                        dimension.screenHeight / 20,
                        animationSpec = tween(time, easing = LinearEasing)
                    )
                    translate.animateTo(
                        -3 * dimension.screenHeight / 20,
                        animationSpec = tween(1, easing = LinearEasing)
                    )
                    if (time > 300) {
                        time -= 25
                    }
                    if (speedReset()) {
                        time = 1500
                    }
                }
            }
        }
        animation()
        launch {
            while (true) {
                while (true) {
                    if (paused()) {
                        translate.stop();break
                    }; delay(200)
                }
                while (true) {
                    if (!paused()) {
                        translate.snapTo(-3 * dimension.screenHeight / 20);time =
                            1500;animation();break
                    }; delay(200)
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.hsv(185f, 0.81f, 1f))
    ) {}
    //The roads with moving tracks and Tap Gesture
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { whereClicked(it.x) } }
    ) {
        drawRect(
            Color.Black,
            topLeft = Offset(dimension.screenWidth / 2 - dimension.screenWidth / 10, 0F),
            size = Size(dimension.screenWidth / 5, dimension.screenHeight)
        )
        drawRect(
            Color.Black,
            topLeft = Offset(dimension.screenWidth / 4 - dimension.screenWidth / 10 - 50F, 0F),
            size = Size(dimension.screenWidth / 5, dimension.screenHeight)
        )
        drawRect(
            Color.Black,
            topLeft = Offset(3 * dimension.screenWidth / 4 - dimension.screenWidth / 10 + 50F, 0F),
            size = Size(dimension.screenWidth / 5, dimension.screenHeight)
        )
        translate(0F, translate.value) {
            for (i in 0..10) {
                drawRect(
                    Color.White,
                    topLeft = Offset(
                        dimension.screenWidth / 2 - dimension.screenWidth / 140,
                        i * dimension.screenHeight / 10
                    ),
                    size = Size(dimension.screenWidth / 70, dimension.screenHeight / 20)
                )
            }
        }
        translate(0F, translate.value) {
            for (i in 0..10) {
                drawRect(
                    Color.White,
                    topLeft = Offset(
                        dimension.screenWidth / 4 - dimension.screenWidth / 140 - 50F,
                        i * dimension.screenHeight / 10
                    ),
                    size = Size(dimension.screenWidth / 70, dimension.screenHeight / 20)
                )
            }
        }
        translate(0F, translate.value) {
            for (i in 0..10) {
                drawRect(
                    Color.White,
                    topLeft = Offset(
                        3 * dimension.screenWidth / 4 - dimension.screenWidth / 140 + 50F,
                        i * dimension.screenHeight / 10
                    ),
                    size = Size(dimension.screenWidth / 70, dimension.screenHeight / 20)
                )
            }
        }
    }
}

@Composable
fun Obstacle_Middle(
    paused: () -> Boolean,
    dimension: WindowInfo,
    delay: (GameStates.Track) -> Int,
    crossedJerry: (GameStates.Track) -> Unit,
    crossedTom: (GameStates.Track) -> Unit,
    speedReset: () -> Boolean
) {
    val yPos = remember { Animatable(-100F) }
    var time = 8000
    var delayTime: Int

    //Animation in while loop and animateTo executes from Coroutine context
    LaunchedEffect(Unit) {
        fun animation() {
            launch {
                while (!paused()) {
                    delayTime = delay(GameStates.Track.Middle)
                    yPos.animateTo(
                        dimension.screenHeight + 100F,
                        animationSpec = tween(
                            time,
                            easing = LinearEasing,
                            delayMillis = delayTime
                        )
                    )
                    yPos.animateTo(-100F, animationSpec = tween(1))
                    if (time >= 3500) {
                        time -= 1500
                    }
                    if (speedReset()) {
                        time = 8000
                    }
                }
            }
        }
        animation()
        launch {
            while (true) {
                while (true) {
                    if (paused()) {
                        yPos.stop();break
                    }; delay(200)
                }
                while (true) {
                    if (!paused()) {
                        yPos.snapTo(-100F);time = 8000;animation();break
                    }; delay(200)
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(dimension.screenWidth / 2 - dimension.screenWidth / 8, yPos.value) {
            drawOval(
                color = Color.hsv(128f, 0.86f, 0.86f),
                size = Size(dimension.screenWidth / 4, dimension.screenWidth / 12)
            )
            translate(dimension.screenWidth / 24, -dimension.screenWidth / 8) {
                drawArc(
                    color = Color.Gray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    size = Size(dimension.screenWidth / 6, dimension.screenHeight / 6)
                )
                drawArc(
                    color = Color.DarkGray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(-dimension.screenWidth / 24, dimension.screenHeight / 30),
                    size = Size(dimension.screenWidth / 12, dimension.screenHeight / 10)
                )
                drawArc(
                    color = Color.LightGray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(dimension.screenWidth / 24, dimension.screenHeight / 24),
                    size = Size(dimension.screenWidth / 6, dimension.screenHeight / 12)
                )
            }

        }

    }

    if (yPos.value > (dimension.screenHeight - 700F)) {
        LaunchedEffect(Unit) { crossedJerry(GameStates.Track.Middle) }
    }
    if (yPos.value > (dimension.screenHeight - 450F)) {
        LaunchedEffect(Unit) { crossedTom(GameStates.Track.Middle) }
    }
}

@Composable
fun Obstacle_Left(
    paused: () -> Boolean,
    dimension: WindowInfo,
    delay: (GameStates.Track) -> Int,
    crossedJerry: (GameStates.Track) -> Unit,
    crossedTom: (GameStates.Track) -> Unit,
    speedReset: () -> Boolean
) {
    val yPos = remember { Animatable(-100F) }
    var time = 8000
    var delayTime: Int
    //Animation in while loop and animateTo executes from Coroutine context
    LaunchedEffect(Unit) {
        fun animation() {
            launch {
                while (!paused()) {
                    delayTime = delay(GameStates.Track.Left)
                    yPos.animateTo(
                        dimension.screenHeight + 100F,
                        animationSpec = tween(time, easing = LinearEasing, delayMillis = delayTime)
                    )
                    yPos.animateTo(-100F, animationSpec = tween(1))
                    if (time >= 3500) {
                        time -= 1500
                    }
                    if (speedReset()) {
                        time = 8000
                    }
                }
            }
        }
        animation()
        launch {
            while (true) {
                while (true) {
                    if (paused()) {
                        yPos.stop();break
                    }; delay(200)
                }
                while (true) {
                    if (!paused()) {
                        yPos.snapTo(-100F);time = 8000;animation();break
                    }; delay(200)
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(dimension.screenWidth / 4 - 50F - dimension.screenWidth / 8, yPos.value) {
            drawOval(
                color = Color.hsv(128f, 0.86f, 0.86f),
                size = Size(dimension.screenWidth / 4, dimension.screenWidth / 12)
            )
            translate(dimension.screenWidth / 24, -dimension.screenWidth / 8) {
                drawArc(
                    color = Color.Gray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    size = Size(dimension.screenWidth / 6, dimension.screenHeight / 6)
                )
                drawArc(
                    color = Color.DarkGray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(-dimension.screenWidth / 24, dimension.screenHeight / 30),
                    size = Size(dimension.screenWidth / 12, dimension.screenHeight / 10)
                )
                drawArc(
                    color = Color.LightGray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(dimension.screenWidth / 24, dimension.screenHeight / 24),
                    size = Size(dimension.screenWidth / 6, dimension.screenHeight / 12)
                )
            }

        }

    }

    if (yPos.value > (dimension.screenHeight - 700F)) {
        LaunchedEffect(Unit) { crossedJerry(GameStates.Track.Left) }
    }
    if (yPos.value > (dimension.screenHeight - 450F)) {
        LaunchedEffect(Unit) { crossedTom(GameStates.Track.Left) }
    }
}

@Composable
fun Obstacle_Right(
    paused: () -> Boolean,
    dimension: WindowInfo,
    delay: (GameStates.Track) -> Int,
    crossedJerry: (GameStates.Track) -> Unit,
    crossedTom: (GameStates.Track) -> Unit,
    speedReset: () -> Boolean
) {
    val yPos = remember { Animatable(-100F) }
    var time = 8000
    var delayTime: Int
    //Animation in while loop and animateTo executes from Coroutine context
    LaunchedEffect(Unit) {
        fun animation() {
            launch {
                while (!paused()) {
                    delayTime = delay(GameStates.Track.Right)
                    yPos.animateTo(
                        dimension.screenHeight + 100F,
                        animationSpec = tween(time, easing = LinearEasing, delayMillis = delayTime)
                    )
                    yPos.animateTo(-100F, animationSpec = tween(1))
                    if (time >= 3500) {
                        time -= 1500
                    }
                    if (speedReset()) {
                        time = 8000
                    }
                }
            }
        }
        animation()
        launch {
            while (true) {
                while (true) {
                    if (paused()) {
                        yPos.stop();break
                    }; delay(200)
                }
                while (true) {
                    if (!paused()) {
                        yPos.snapTo(-100F);time = 8000;animation();break
                    }; delay(200)
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(3 * dimension.screenWidth / 4 + 50F - dimension.screenWidth / 8, yPos.value) {
            drawOval(
                color = Color.hsv(128f, 0.86f, 0.86f),
                size = Size(dimension.screenWidth / 4, dimension.screenWidth / 12)
            )
            translate(dimension.screenWidth / 24, -dimension.screenWidth / 8) {
                drawArc(
                    color = Color.Gray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    size = Size(dimension.screenWidth / 6, dimension.screenHeight / 6)
                )
                drawArc(
                    color = Color.DarkGray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(-dimension.screenWidth / 24, dimension.screenHeight / 30),
                    size = Size(dimension.screenWidth / 12, dimension.screenHeight / 10)
                )
                drawArc(
                    color = Color.LightGray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(dimension.screenWidth / 24, dimension.screenHeight / 24),
                    size = Size(dimension.screenWidth / 6, dimension.screenHeight / 12)
                )
            }

        }

    }

    if (yPos.value > (dimension.screenHeight - 700F)) {
        LaunchedEffect(Unit) { crossedJerry(GameStates.Track.Right) }
    }
    if (yPos.value > (dimension.screenHeight - 450F)) {
        LaunchedEffect(Unit) { crossedTom(GameStates.Track.Right) }
    }
}

@Composable
fun Score(score: Int, dimension: WindowInfo) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 16.dp)
    ) {
        drawRoundRect(
            Color.LightGray,
            size = Size(400F, 100F),
            cornerRadius = CornerRadius(40F, 40F),
            topLeft = Offset(dimension.screenWidth - 450F, 0F)
        )
    }
    Image(
        painter = painterResource(id = R.drawable.star), contentDescription = null,
        modifier = Modifier
            .size(35.dp, 35.dp)
            .offset(250.dp, 15.dp)
    )
    Text(
        "$score", modifier = Modifier
            .fillMaxSize()
            .offset(310.dp, 15.dp), fontSize = 25.sp
    )
}

@Composable
fun CheeseScore(cheese: Int) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 16.dp)
    ) {
        drawRoundRect(
            Color.LightGray,
            size = Size(400F, 100F),
            cornerRadius = CornerRadius(40F, 40F),
            topLeft = Offset(12F, 0F)
        )
    }
    Image(
        painter = painterResource(id = R.drawable.cheese), contentDescription = null,
        modifier = Modifier
            .size(35.dp, 35.dp)
            .offset(18.dp, 15.dp)
    )
    Text(
        "$cheese", modifier = Modifier
            .fillMaxSize()
            .offset(85.dp, 15.dp), fontSize = 25.sp
    )
}

val list = listOf(GameStates.Track.Middle, GameStates.Track.Left, GameStates.Track.Right)

@Composable
fun Heart(
    show: Boolean,
    paused: () -> Boolean,
    dimension: WindowInfo,
    delay: (GameStates.Track) -> Int,
    heartJerry: (GameStates.Track) -> Unit,
    speedReset: () -> Boolean
) {
    val yPos = remember { Animatable(-60F) }
    val xPos = remember { Animatable((dimension.screenWidthinDp / 2 - 25.dp).value) }
    var alpha by remember { mutableFloatStateOf(1f) }
    var time = 22000
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            fun animation() {
                launch {
                    while (!paused()) {
                        val track = list.random()
                        val delayTime = delay(track) + 2500
                        when (track) {
                            GameStates.Track.Middle -> {
                                xPos.animateTo(
                                    (dimension.screenWidthinDp / 2 - 25.dp).value,
                                    tween(1)
                                )
                            }

                            GameStates.Track.Left -> {
                                xPos.animateTo(
                                    (dimension.screenWidthinDp / 4 - 45.dp).value,
                                    tween(1)
                                )
                            }

                            GameStates.Track.Right -> {
                                xPos.animateTo(
                                    (3 * dimension.screenWidthinDp / 4 - 10.dp).value,
                                    tween(1)
                                )
                            }
                        }
                        yPos.animateTo(
                            2500F,
                            tween(time, easing = LinearEasing, delayMillis = delayTime)
                        ).endState
                        if (time > 10000) {
                            time -= 8000
                        }
                        if (speedReset()) {
                            time = 22000
                        }
                        yPos.animateTo(-60F, tween(1, easing = LinearEasing))
                    }
                }
            }
            animation()
            launch {
                while (true) {
                    while (true) {
                        if (paused()) {
                            yPos.stop();break
                        }
                    }
                    while (true) {
                        if (!paused()) {
                            yPos.snapTo(-60F);time = 22000;animation();break
                        }
                    }
                }
            }
        }
    }
    alpha = if (show) {
        0f
    } else {
        1f
    }
    Image(
        painter = painterResource(id = R.drawable.heart), contentDescription = null,
        modifier = Modifier
            .size(50.dp, 50.dp)
            .offset(xPos.value.dp, yPos.value.dp)
            .alpha(alpha)
    )
    if (yPos.value.toInt() in ((dimension.screenHeightinDp - 250.dp).value.toInt())..(dimension.screenHeightinDp - 180.dp).value.toInt()) {
        when (xPos.value) {
            (53.25F) -> {
                heartJerry(GameStates.Track.Left)
            }

            (284.75F) -> {
                heartJerry(GameStates.Track.Right)
            }

            (171.5F) -> {
                heartJerry(GameStates.Track.Middle)
            }
        }
    }
}

@Composable
fun HeartTime(time: Int, dimension: WindowInfo) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 16.dp)
    ) {
        drawRoundRect(
            Color.LightGray,
            size = Size(175F, 100F),
            cornerRadius = CornerRadius(40F, 40F),
            topLeft = Offset(dimension.screenWidth / 2 - 105, 0F)
        )
    }
    Image(
        painter = painterResource(id = R.drawable.heart), contentDescription = null,
        modifier = Modifier
            .size(20.dp, 20.dp)
            .offset(dimension.screenWidthinDp / 2 - 24.dp, 22.dp)
    )

    Text(
        "$time", modifier = Modifier
            .fillMaxSize()
            .offset(dimension.screenWidthinDp / 2, 18.dp), fontSize = 20.sp
    )
}

@Composable
fun Trap(
    show: Boolean,
    paused: () -> Boolean,
    dimension: WindowInfo,
    delay: (GameStates.Track) -> Int,
    trapJerry: (GameStates.Track) -> Unit,
    speedReset: () -> Boolean
) {
    val yPos = remember { Animatable(-60F) }
    val xPos = remember { Animatable((dimension.screenWidthinDp / 2 - 25.dp).value) }
    var alpha by remember { mutableFloatStateOf(1f) }
    var time = 22000
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            fun animation() {
                launch {
                    while (!paused()) {
                        val track = list.random()
                        val delayTime = delay(track) + 6000
                        when (track) {
                            GameStates.Track.Middle -> {
                                xPos.animateTo(
                                    (dimension.screenWidthinDp / 2 - 25.dp).value,
                                    tween(1)
                                )
                            }

                            GameStates.Track.Left -> {
                                xPos.animateTo(
                                    (dimension.screenWidthinDp / 4 - 45.dp).value,
                                    tween(1)
                                )
                            }

                            GameStates.Track.Right -> {
                                xPos.animateTo(
                                    (3 * dimension.screenWidthinDp / 4 - 10.dp).value,
                                    tween(1)
                                )
                            }
                        }
                        yPos.animateTo(
                            2500F,
                            tween(time, easing = LinearEasing, delayMillis = delayTime)
                        ).endState
                        if (time > 10000) {
                            time -= 8000
                        }
                        if (speedReset()) {
                            time = 22000
                        }
                        yPos.animateTo(-60F, tween(1, easing = LinearEasing))
                    }
                }
            }
            animation()
            launch {

                while (true) {
                    while (true) {
                        if (paused()) {
                            yPos.stop();break
                        }
                    }
                    while (true) {
                        if (!paused()) {
                            yPos.snapTo(-60F);time = 22000;animation();break
                        }
                    }
                }
            }
        }
    }
    alpha = if (show) {
        0f
    } else {
        1f
    }
    Image(
        painter = painterResource(id = R.drawable.box), contentDescription = null,
        modifier = Modifier
            .size(50.dp, 50.dp)
            .offset(xPos.value.dp, yPos.value.dp)
            .alpha(alpha)
    )
    if (yPos.value.toInt() in ((dimension.screenHeightinDp - 250.dp).value.toInt())..(dimension.screenHeightinDp - 180.dp).value.toInt()) {
        when (xPos.value) {
            (53.25F) -> {
                trapJerry(GameStates.Track.Left)
            }

            (284.75F) -> {
                trapJerry(GameStates.Track.Right)
            }

            (171.5F) -> {
                trapJerry(GameStates.Track.Middle)
            }
        }
    }
}

@Composable
fun Cheese(
    show: Boolean,
    paused: () -> Boolean,
    dimension: WindowInfo,
    delay: (GameStates.Track) -> Int,
    cheeseJerry: (GameStates.Track) -> Unit,
    speedReset: () -> Boolean
) {
    val yPos = remember { Animatable(-60F) }
    val xPos = remember { Animatable((dimension.screenWidthinDp / 2 - 25.dp).value) }
    var alpha by remember { mutableFloatStateOf(1f) }
    var time = 22000
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            fun animation() {
                launch {
                    while (!paused()) {
                        val track = list.random()
                        val delayTime = delay(track) + 8000
                        when (track) {
                            GameStates.Track.Middle -> {
                                xPos.animateTo(
                                    (dimension.screenWidthinDp / 2 - 25.dp).value,
                                    tween(1)
                                )
                            }

                            GameStates.Track.Left -> {
                                xPos.animateTo(
                                    (dimension.screenWidthinDp / 4 - 45.dp).value,
                                    tween(1)
                                )
                            }

                            GameStates.Track.Right -> {
                                xPos.animateTo(
                                    (3 * dimension.screenWidthinDp / 4 - 10.dp).value,
                                    tween(1)
                                )
                            }
                        }
                        yPos.animateTo(
                            2500F,
                            tween(time, easing = LinearEasing, delayMillis = delayTime)
                        ).endState
                        if (time > 10000) {
                            time -= 8000
                        }
                        if (speedReset()) {
                            time = 22000
                        }
                        yPos.animateTo(-60F, tween(1, easing = LinearEasing))
                    }
                }
            }
            animation()
            launch {
                while (true) {
                    while (true) {
                        if (paused()) {
                            yPos.stop();break
                        }
                    }
                    while (true) {
                        if (!paused()) {
                            yPos.snapTo(-60F);time = 22000;animation();break
                        }
                    }
                }
            }
        }
    }
    alpha = if (show) {
        0f
    } else {
        1f
    }
    Image(
        painter = painterResource(id = R.drawable.cheese), contentDescription = null,
        modifier = Modifier
            .size(50.dp, 50.dp)
            .offset(xPos.value.dp, yPos.value.dp)
            .alpha(alpha)
    )
    if (yPos.value.toInt() in ((dimension.screenHeightinDp - 250.dp).value.toInt())..(dimension.screenHeightinDp - 180.dp).value.toInt()) {
        when (xPos.value) {
            (53.25F) -> {
                cheeseJerry(GameStates.Track.Left)
            }

            (284.75F) -> {
                cheeseJerry(GameStates.Track.Right)
            }

            (171.5F) -> {
                cheeseJerry(GameStates.Track.Middle)
            }
        }
    }
}

@Composable
fun gameover(useCheese: () -> Unit, playagain: () -> Unit, home: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.7f)
            .background(Color.LightGray)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp, 245.dp)
            .background(Color.White, RoundedCornerShape(35.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.gameover),
            contentDescription = null,
            modifier = Modifier.size(100.dp, 85.dp)
        )

        Text(
            text = "Jerry got caught :((",
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight(1000),
            fontSize = 25.sp
        )
        Button(
            onClick = { useCheese() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp)
                .height(50.dp),
            colors = ButtonDefaults
                .buttonColors(containerColor = Color.hsv(30f, 1f, 0.59f))
        )
        {
            Image(
                painter = painterResource(id = R.drawable.cheese), contentDescription = null,
                modifier = Modifier
                    .size(50.dp, 50.dp)
            )
            Text(
                text = "Use Cheese",
                color = Color.hsv(62f, 1f, 1f),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(1000),
                fontSize = 20.sp
            )
        }
        Button(
            onClick = { playagain() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp)
                .height(50.dp),
            colors = ButtonDefaults
                .buttonColors(containerColor = Color.hsv(195f, 0.80f, 0.94f))
        )
        {
            Text(
                text = "Play Again",
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(1000),
                fontSize = 20.sp
            )
        }
        Button(
            onClick = { home() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp)
                .height(50.dp),
            colors = ButtonDefaults
                .buttonColors(containerColor = Color.hsv(3f, 0.66f, 1f))
        )
        {
            Text(
                text = "Home",
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(1000),
                fontSize = 20.sp
            )
        }

    }
}
