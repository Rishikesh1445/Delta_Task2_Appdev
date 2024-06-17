package com.example.cheesechase

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times

@Composable
fun trackAnimation(dimension:WindowInfo, whereClicked: (Float)->Unit, speedReset:()->Boolean){
    //Animation
    val translate = remember{ Animatable(-3 * dimension.screenHeight / 20) }
    var time=1500
    LaunchedEffect(Unit){
        while (true){
            translate.animateTo(dimension.screenHeight/20 , animationSpec = tween(time, easing = LinearEasing))
            translate.animateTo(-3 * dimension.screenHeight / 20,animationSpec = tween(1, easing = LinearEasing))
            if(time>300) { time -= 25}
            if(speedReset()){time = 1500}
        }
    }

    //The roads with moving tracks and Tap Gesture
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { whereClicked(it.x) } }
    ){
        drawRect(
            Color.Black,
            topLeft = Offset(dimension.screenWidth/2 - dimension.screenWidth/10 , 0F),
            size = Size(dimension.screenWidth/5,dimension.screenHeight)
        )
        drawRect(
            Color.Black,
            topLeft = Offset(dimension.screenWidth/4 - dimension.screenWidth/10 -50F , 0F),
            size = Size(dimension.screenWidth/5,dimension.screenHeight)
        )
        drawRect(
            Color.Black,
            topLeft = Offset(3*dimension.screenWidth/4 - dimension.screenWidth/10 +50F, 0F),
            size = Size(dimension.screenWidth/5,dimension.screenHeight)
        )
        translate(0F,translate.value) {
            for (i in 0..10) {
                drawRect(
                    Color.White,
                    topLeft = Offset(dimension.screenWidth/2-dimension.screenWidth/140 , i*dimension.screenHeight/10),
                    size = Size(dimension.screenWidth/70, dimension.screenHeight/20)
                )
            }
        }
        translate(0F,translate.value) {
            for (i in 0..10) {
                drawRect(
                    Color.White,
                    topLeft = Offset(dimension.screenWidth/4-dimension.screenWidth/140 -50F , i*dimension.screenHeight/10),
                    size = Size(dimension.screenWidth/70, dimension.screenHeight/20)
                )
            }
        }
        translate(0F,translate.value) {
            for (i in 0..10) {
                drawRect(
                    Color.White,
                    topLeft = Offset(3*dimension.screenWidth/4 - dimension.screenWidth/140 +50F, i*dimension.screenHeight/10),
                    size = Size(dimension.screenWidth/70, dimension.screenHeight/20)
                )
            }
        }
    }
}

@Composable
fun Obstacle_Middle(dimension: WindowInfo,delay:(States.Track)->Int, crossedJerry:(States.Track)->Unit , crossedTom:(States.Track)->Unit, show:(Boolean)->Unit, speedReset:()->Boolean){
    val yPos = remember{ Animatable(-100F)}
    var time = 8000
    var delayTime :Int

    //Animation in while loop and animateTo executes from Coroutine context
    LaunchedEffect(Unit){
        while (true){
            delayTime = delay(States.Track.Middle)
            yPos.animateTo(dimension.screenHeight+100F , animationSpec = tween(time, easing = LinearEasing, delayMillis = delayTime))
            yPos.animateTo(-100F, animationSpec = tween(1))
            if(time < 3500){show(true)}
            if(time>=3500){time -= 1500; show(false)}
            if(speedReset()){time = 8000 ; show(false)}
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(dimension.screenWidth/2 - dimension.screenWidth/8, yPos.value) {
            drawOval(
                color = Color.hsv(128f, 0.86f, 0.86f),
                size = Size(dimension.screenWidth/4, dimension.screenWidth/12)
            )
            translate(dimension.screenWidth/24 , -dimension.screenWidth/8) {
                drawArc(
                    color = Color.Gray,
                    startAngle =180f,
                    sweepAngle =180f,
                    useCenter = false,
                    size = Size(dimension.screenWidth/6, dimension.screenHeight/6)
                )
                drawArc(
                    color = Color.DarkGray,
                    startAngle =180f,
                    sweepAngle =180f,
                    useCenter = false,
                    topLeft = Offset(-dimension.screenWidth/24 , dimension.screenHeight/30),
                    size = Size(dimension.screenWidth/12, dimension.screenHeight/10)
                )
                drawArc(
                    color = Color.LightGray,
                    startAngle =180f,
                    sweepAngle =180f,
                    useCenter = false,
                    topLeft = Offset(dimension.screenWidth/24 , dimension.screenHeight/24),
                    size = Size(dimension.screenWidth/6, dimension.screenHeight/12)
                )
            }

        }

    }

    if(yPos.value > (dimension.screenHeight - 700F)){
        LaunchedEffect(Unit){crossedJerry(States.Track.Middle) }
    }
    if(yPos.value > (dimension.screenHeight - 450F)){
        LaunchedEffect(Unit){crossedTom(States.Track.Middle)}
    }
}

@Composable
fun Obstacle_Left(dimension: WindowInfo,delay:(States.Track)->Int, crossedJerry:(States.Track)->Unit , crossedTom:(States.Track)->Unit, show:(Boolean)->Unit, speedReset:()->Boolean){
    val yPos = remember{ Animatable(-100F)}
    var time = 8000
    var delayTime:Int
    //Animation in while loop and animateTo executes from Coroutine context
    LaunchedEffect(Unit){
        while (true){
            delayTime = delay(States.Track.Left)
            yPos.animateTo(dimension.screenHeight+100F , animationSpec = tween(time, easing = LinearEasing, delayMillis = delayTime))
            yPos.animateTo(-100F, animationSpec = tween(1))
            if(time < 3500){show(true)}
            if(time>=3500){time -= 1500; show(false)}
            if(speedReset()){time = 8000 ; show(false)}
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(dimension.screenWidth/4 -50F - dimension.screenWidth/8, yPos.value) {
            drawOval(
                color = Color.hsv(128f, 0.86f, 0.86f),
                size = Size(dimension.screenWidth/4, dimension.screenWidth/12)
            )
            translate(dimension.screenWidth/24 , -dimension.screenWidth/8) {
                drawArc(
                    color = Color.Gray,
                    startAngle =180f,
                    sweepAngle =180f,
                    useCenter = false,
                    size = Size(dimension.screenWidth/6, dimension.screenHeight/6)
                )
                drawArc(
                    color = Color.DarkGray,
                    startAngle =180f,
                    sweepAngle =180f,
                    useCenter = false,
                    topLeft = Offset(-dimension.screenWidth/24 , dimension.screenHeight/30),
                    size = Size(dimension.screenWidth/12, dimension.screenHeight/10)
                )
                drawArc(
                    color = Color.LightGray,
                    startAngle =180f,
                    sweepAngle =180f,
                    useCenter = false,
                    topLeft = Offset(dimension.screenWidth/24 , dimension.screenHeight/24),
                    size = Size(dimension.screenWidth/6, dimension.screenHeight/12)
                )
            }

        }

    }

    if(yPos.value > (dimension.screenHeight - 700F)){
        LaunchedEffect(Unit){crossedJerry(States.Track.Left) }
    }
    if(yPos.value > (dimension.screenHeight - 450F)){
        LaunchedEffect(Unit){crossedTom(States.Track.Left)}
    }
}

@Composable
fun Obstacle_Right(dimension: WindowInfo,delay:(States.Track)->Int, crossedJerry:(States.Track)->Unit , crossedTom:(States.Track)->Unit,  show:(Boolean)->Unit, speedReset:()->Boolean){
    val yPos = remember{ Animatable(-100F)}
    var time = 8000
    var delayTime:Int
    //Animation in while loop and animateTo executes from Coroutine context
    LaunchedEffect(Unit){
        while (true){
            delayTime = delay(States.Track.Right)
            yPos.animateTo(dimension.screenHeight+100F , animationSpec = tween(time, easing = LinearEasing, delayMillis = delayTime))
            yPos.animateTo(-100F, animationSpec = tween(1))
            if(time < 3500){show(true)}
            if(time>=3500){time -= 1500 ; show(false)}
            if(speedReset()){time = 8000 ; show(false)}
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(3*dimension.screenWidth/4 +50F - dimension.screenWidth/8, yPos.value) {
            drawOval(
                color = Color.hsv(128f, 0.86f, 0.86f),
                size = Size(dimension.screenWidth/4, dimension.screenWidth/12)
            )
            translate(dimension.screenWidth/24 , -dimension.screenWidth/8) {
                drawArc(
                    color = Color.Gray,
                    startAngle =180f,
                    sweepAngle =180f,
                    useCenter = false,
                    size = Size(dimension.screenWidth/6, dimension.screenHeight/6)
                )
                drawArc(
                    color = Color.DarkGray,
                    startAngle =180f,
                    sweepAngle =180f,
                    useCenter = false,
                    topLeft = Offset(-dimension.screenWidth/24 , dimension.screenHeight/30),
                    size = Size(dimension.screenWidth/12, dimension.screenHeight/10)
                )
                drawArc(
                    color = Color.LightGray,
                    startAngle =180f,
                    sweepAngle =180f,
                    useCenter = false,
                    topLeft = Offset(dimension.screenWidth/24 , dimension.screenHeight/24),
                    size = Size(dimension.screenWidth/6, dimension.screenHeight/12)
                )
            }

        }

    }

    if(yPos.value > (dimension.screenHeight - 700F)){
        LaunchedEffect(Unit){crossedJerry(States.Track.Right) }
    }
    if(yPos.value > (dimension.screenHeight - 450F)){
        LaunchedEffect(Unit){crossedTom(States.Track.Right)}
    }
}

@Composable
fun Score(score:Int , dimension: WindowInfo){
    Canvas(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp, 16.dp)){
        drawRoundRect(
            Color.LightGray,
            size = Size(400F, 100F),
            cornerRadius = CornerRadius(40F, 40F),
            topLeft = Offset(dimension.screenWidth-450F, 0F)
        )
    }
    Image(painter = painterResource(id = R.drawable.star), contentDescription = null,
        modifier = Modifier
            .size(35.dp, 35.dp)
            .offset(250.dp, 15.dp)
    )
    Text("$score" , modifier = Modifier
        .fillMaxSize()
        .offset(310.dp, 15.dp), fontSize = 25.sp )
}

@Composable
fun CheeseScore(cheese:Int){
    Canvas(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp, 16.dp)){
        drawRoundRect(
            Color.LightGray,
            size = Size(400F, 100F),
            cornerRadius = CornerRadius(40F, 40F),
            topLeft = Offset(12F, 0F)
        )
    }
    Image(painter = painterResource(id = R.drawable.cheese), contentDescription = null,
        modifier = Modifier
            .size(35.dp, 35.dp)
            .offset(18.dp, 15.dp)
    )
    Text("$cheese" , modifier = Modifier
        .fillMaxSize()
        .offset(85.dp, 15.dp), fontSize = 25.sp )
}

val list = listOf(States.Track.Middle, States.Track.Left, States.Track.Right)

@Composable
fun Heart(show:Boolean, dimension: WindowInfo, delay: (States.Track) -> Int , heartJerry:(States.Track)->Unit){
    if(show){
        val yPos = remember{ Animatable(-60F)}
        val xPos = remember { Animatable((dimension.screenWidthinDp/2-25.dp).value) }
        LaunchedEffect(Unit){
            while(true){
                val track = list.random()
                val delayTime = delay(track)
                when (track) {
                    States.Track.Middle -> { xPos.animateTo((dimension.screenWidthinDp/2-25.dp).value , tween(1)) }
                    States.Track.Left -> { xPos.animateTo((dimension.screenWidthinDp/4-45.dp).value , tween(1)) }
                    States.Track.Right -> { xPos.animateTo((3*dimension.screenWidthinDp/4 - 10.dp).value , tween(1))}
                }
                yPos.animateTo(2500F , tween(10000, easing = LinearEasing, delayMillis = delayTime))
                yPos.animateTo(-60F , tween(1, easing = LinearEasing))

            }
        }
        Image(
            painter = painterResource(id = R.drawable.heart), contentDescription = null,
            modifier = Modifier
                .size(50.dp, 50.dp)
                .offset(xPos.value.dp, yPos.value.dp)
        )
        if(yPos.value > (dimension.screenHeightinDp -250.dp).value) {
            when (xPos.value) {
                (53.25F) -> {
                    heartJerry(States.Track.Left)
                }
                (284.75F) -> {
                    heartJerry(States.Track.Right)
                }
                (171.5F) -> {
                    heartJerry(States.Track.Middle)
                }
            }
        }
    }
}
@Composable
fun HeartTime(time:Int , dimension: WindowInfo){
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 16.dp)
        ) {
            drawRoundRect(
                Color.LightGray,
                size = Size(175F, 100F),
                cornerRadius = CornerRadius(40F, 40F),
                topLeft = Offset(dimension.screenWidth/2- 105, 0F)
            )
        }
    Image(painter = painterResource(id = R.drawable.heart), contentDescription = null,
        modifier = Modifier.size(20.dp, 20.dp).offset(dimension.screenWidthinDp/2- 24.dp, 22.dp))

    Text(
        "$time", modifier = Modifier
            .fillMaxSize()
            .offset(dimension.screenWidthinDp/2, 18.dp), fontSize = 20.sp
    )
}

@Composable
fun Trap(show:Boolean, dimension: WindowInfo, delay: (States.Track) -> Int , trapJerry:(States.Track)->Unit){
    if(show){
        val yPos = remember{ Animatable(-60F)}
        val xPos = remember { Animatable((dimension.screenWidthinDp/2-25.dp).value) }
        LaunchedEffect(Unit){
            while(true){
                val track = list.random()
                val delayTime = delay(track)
                when (track) {
                    States.Track.Middle -> { xPos.animateTo((dimension.screenWidthinDp/2-25.dp).value , tween(1)) }
                    States.Track.Left -> { xPos.animateTo((dimension.screenWidthinDp/4-45.dp).value , tween(1)) }
                    States.Track.Right -> { xPos.animateTo((3*dimension.screenWidthinDp/4 - 10.dp).value , tween(1))}
                }
                yPos.animateTo(2500F , tween(10000, easing = LinearEasing, delayMillis = delayTime))
                yPos.animateTo(-60F , tween(1, easing = LinearEasing))

            }
        }
        Image(
            painter = painterResource(id = R.drawable.box), contentDescription = null,
            modifier = Modifier
                .size(50.dp, 50.dp)
                .offset(xPos.value.dp, yPos.value.dp)
        )
        if(yPos.value > (dimension.screenHeightinDp -250.dp).value) {
            when (xPos.value) {
                (53.25F) -> {
                    trapJerry(States.Track.Left)
                }
                (284.75F) -> {
                    trapJerry(States.Track.Right)
                }
                (171.5F) -> {
                    trapJerry(States.Track.Middle)
                }
            }
        }
    }
}

@Composable
fun Cheese(show:Boolean, dimension: WindowInfo, delay: (States.Track) -> Int , cheeseJerry:(States.Track)->Unit){
    if(show){
        val yPos = remember{ Animatable(-60F)}
        val xPos = remember { Animatable((dimension.screenWidthinDp/2-25.dp).value) }
        LaunchedEffect(Unit){
            while(true){
                val track = list.random()
                val delayTime = delay(track)
                when (track) {
                    States.Track.Middle -> { xPos.animateTo((dimension.screenWidthinDp/2-25.dp).value , tween(1)) }
                    States.Track.Left -> { xPos.animateTo((dimension.screenWidthinDp/4-45.dp).value , tween(1)) }
                    States.Track.Right -> { xPos.animateTo((3*dimension.screenWidthinDp/4 - 10.dp).value , tween(1))}
                }
                yPos.animateTo(2500F , tween(10000, easing = LinearEasing, delayMillis = delayTime))
                yPos.animateTo(-60F , tween(1, easing = LinearEasing))

            }
        }
        Image(
            painter = painterResource(id = R.drawable.cheese), contentDescription = null,
            modifier = Modifier
                .size(50.dp, 50.dp)
                .offset(xPos.value.dp, yPos.value.dp)
        )
        if(yPos.value > (dimension.screenHeightinDp -250.dp).value) {
            when (xPos.value) {
                (53.25F) -> {
                    cheeseJerry(States.Track.Left)
                }
                (284.75F) -> {
                    cheeseJerry(States.Track.Right)
                }
                (171.5F) -> {
                    cheeseJerry(States.Track.Middle)
                }
            }
        }
    }
}

@Composable
fun gameover(useCheese:()->Unit , playagain:()->Unit, home:()->Unit) {
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
            contentDescription =null,
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
                color = Color.hsv(62f ,1f, 1f),
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
