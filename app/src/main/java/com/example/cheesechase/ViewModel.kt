package com.example.cheesechase

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel:ViewModel() {
    private val _state = mutableStateOf(States())
    val state: State<States> = _state

    fun gamePause(){
        _state.value = _state.value.copy(gamePause = !state.value.gamePause)
    }
    fun sendgamePause():Boolean{
        return state.value.gamePause
    }
    fun gameOverFunction(){
        if(state.value.jerryTouched>1){
            _state.value = _state.value.copy(gamePause = true, gameOver = true)
        }
    }
    fun playAgain(dimension: WindowInfo){
        _state.value = _state.value.copy(gamePause = false, gameOver = false , highscore = 0, cheeseScore = 0, jerryTouched = 0, heartTime = 0)
        changeTrack(States.Track.Middle, dimension)
    }
    fun useCheese(){
        if(state.value.cheeseScore>0) {
            _state.value = _state.value.copy(gamePause = false, gameOver = false, jerryTouched = 0, cheeseScore = state.value.cheeseScore-1)
        }
    }

    fun gyrobutton(){
        _state.value = _state.value.copy(gyroMode = !state.value.gyroMode, touchMode = !state.value.touchMode)
    }
    
    fun openingAnimation(dimension: WindowInfo) {
        _state.value = _state.value.copy(
            jerryPositiony = dimension.screenHeightinDp - 275.dp,
            tomPositiony = dimension.screenHeightinDp - 150.dp
        )
        if (state.value.jerryTouched == 0 && state.value.highscore > 0) {
            _state.value = _state.value.copy(tomPositionyTiming = 2000, tomPositiony = 1000.dp)
        }
    }

    fun closeToJerry(dimension: WindowInfo) {
        if (state.value.jerryTouched == 1) {
            _state.value = _state.value.copy(
                tomPositionyTiming = 200,
                tomPositiony = dimension.screenHeightinDp - 150.dp
            )
        }
        if (state.value.jerryTouched > 1) {
            _state.value = _state.value.copy(
                tomPositionyTiming = 200,
                tomPositiony = dimension.screenHeightinDp - 210.dp
            )
        }

    }

    //Tap Gesture Function
    fun trackClicked(where: Float, dimension: WindowInfo) {
        if (state.value.touchMode) {
            when (where) {
                in (dimension.screenWidth / 2 - dimension.screenWidth / 10)..dimension.screenWidth / 2 + dimension.screenWidth / 10 -> {
                    changeTrack(States.Track.Middle, dimension)
                }

                in (dimension.screenWidth / 4 - dimension.screenWidth / 10 - 50F)..dimension.screenWidth / 4 + dimension.screenWidth / 10 - 50F -> {
                    changeTrack(States.Track.Left, dimension)
                }

                in (3 * dimension.screenWidth / 4 - dimension.screenWidth / 10 + 50F)..3 * dimension.screenWidth / 4 + dimension.screenWidth / 10 + 50F -> {
                    changeTrack(States.Track.Right, dimension)
                }
            }
        }
    }

    //CHANGE NAME AT LAST IF NEEDED
    fun startGame(gyroscope: Gyroscope, dimension: WindowInfo) {
        viewModelScope.launch {
            //This Property Belongs to Gyroscope
            try {
                    var phoneRotation = 0F
                    delay(1000)
                    var changeTrack: States.Track
                    val Yaxis = gyroscope.getGyroscopeData().map { it.copy(y = it.y) }
                    Yaxis.collect {
                        //Gyro gives in rad/s . multiplied with time 1 sec and 100 just for multiplicative factor
                        phoneRotation += it.y * 100F
                        changeTrack = when {
                            phoneRotation > 700F -> {
                                States.Track.Right
                            }

                            phoneRotation < (-500F) -> {
                                States.Track.Left
                            }

                            else -> {
                                States.Track.Middle
                            }
                        }
                        changeTrack(changeTrack, dimension)

                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if(!state.value.gyroMode){viewModelScope.cancel()}
        }
    }

    //this function ensures objects are copied only once if there is a change in track
    private fun changeTrack(changeTrack: States.Track, dimension: WindowInfo) {
        if (state.value.jerryTrack != changeTrack) {
            when (changeTrack) {
                States.Track.Middle -> {
                    _state.value = _state.value.copy(
                        jerryPositionx = dimension.screenWidthinDp / 2 - 52.dp,
                        tomPositionx = dimension.screenWidthinDp / 2 - 52.dp,
                        jerryTrack = changeTrack
                    )
                }

                States.Track.Left -> {
                    _state.value = _state.value.copy(
                        jerryPositionx = dimension.screenWidthinDp / 4 - 68.dp,
                        tomPositionx = dimension.screenWidthinDp / 4 - 68.dp,
                        jerryTrack = changeTrack
                    )
                }

                States.Track.Right -> {
                    _state.value = _state.value.copy(
                        jerryPositionx = dimension.screenWidthinDp / 2 + 64.dp,
                        tomPositionx = dimension.screenWidthinDp / 2 + 64.dp,
                        jerryTrack = changeTrack
                    )
                }
            }
        }
    }

    //Obstacle Crossing Jerry's Y position
    fun obstacleCrossed(track: States.Track, context: Context) {
        if (track == state.value.jerryTrack && !state.value.jerryJump &&!state.value.heart) {
            vibration(context)
            _state.value = _state.value.copy(jerryTouched = state.value.jerryTouched + 1)
        } else {
            _state.value = _state.value.copy(highscore = state.value.highscore + 1)
        }
    }

    //Auto Jump For Tom
    fun tomJump(track: States.Track) {
        if (track == state.value.jerryTrack) {
            _state.value = _state.value.copy(tomJump = true)
        }
    }

    //Clickable Event for Jerry
    fun jumpJerry() {
        _state.value = _state.value.copy(jerryJump = true)
    }

    //Forced to keep these below 2 function separately coz LaunchedEffect needs composable function.
    @Composable
    fun JerryJumping(context: Context) {
        LaunchedEffect(Unit) {
            vibration(context)
            _state.value = _state.value.copy(
                jerrySize = 160.dp,
                jerryPositionx = state.value.jerryPositionx - 25.dp
            )
            delay(200)
            _state.value = _state.value.copy(
                jerrySize = 110.dp,
                jerryPositionx = state.value.jerryPositionx + 25.dp,
                jerryJump = false
            )
        }
    }
    @Composable
    fun TomJumping() {
        LaunchedEffect(Unit) {
            _state.value =
                _state.value.copy(tomSize = 160.dp, tomPositionx = state.value.tomPositionx - 25.dp)
            delay(200)
            _state.value = _state.value.copy(
                tomSize = 110.dp,
                tomPositionx = state.value.tomPositionx + 25.dp,
                tomJump = false
            )
        }
    }

    private var previousMiddle= 0
    private var previousLeft= 0
    private var previousRight= 0
    private val list = (2000..8000 step 1500).toList()
    fun delay(track: States.Track):Int{
        var random = list.random()
        return when(track){
            States.Track.Middle ->{while(previousMiddle==random){random = list.random()} ; previousMiddle=random; random }
            States.Track.Right ->{while(previousRight==random){random = list.random()} ; previousRight=random; random }
            States.Track.Left ->{while(previousLeft==random){random = list.random()} ; previousLeft=random; random }
        }
    }

    fun heartJerryBool(track: States.Track){
        if (track == state.value.jerryTrack && !state.value.jerryJump) {
            _state.value = _state.value.copy(heart = true, heartTime = 15)
        }
    }
    fun trapJerryBool(track: States.Track){
        if (track == state.value.jerryTrack && !state.value.jerryJump) {
            _state.value = _state.value.copy(trap = true)
        }
    }
    fun cheeseJerryBool(track: States.Track){
        if (track == state.value.jerryTrack && !state.value.jerryJump) {
            _state.value = _state.value.copy(cheese = true)
        }
    }

    @Composable
    fun HeartJerryTime(){
        if(state.value.heart) {
            LaunchedEffect(Unit) {
                while (state.value.heartTime > 0) {
                    if(!state.value.gamePause) {
                        delay(1000)
                        _state.value = _state.value.copy(heartTime = state.value.heartTime - 1)
                    }
                }
            }
            if (state.value.heartTime == 0) {
                _state.value = _state.value.copy(heart = false)
            }
        }
    }

    @Composable
    fun trapRandom(){
        if(state.value.trap) {
            LaunchedEffect(Unit) {
                val random = Random.nextBoolean()
                if (random) {
                    _state.value = _state.value.copy(jerryTouched = state.value.jerryTouched + 1)
                    delay(5000)
                    _state.value = _state.value.copy(trap = false)
                } else {

                        _state.value = _state.value.copy(speedReset = true)
                        delay(5000)
                        _state.value = _state.value.copy(speedReset = false, trap = false)

                }
            }
        }
    }
    fun speedReset():Boolean{
        return state.value.speedReset
    }

    @Composable
    fun CheeseCount(){
        if(state.value.cheese){
            LaunchedEffect(Unit){_state.value= _state.value.copy(cheeseScore = state.value.cheeseScore+1)
            delay(5000)
            _state.value = _state.value.copy(cheese = false)}
        }
    }

    //Vibrator
    private fun vibration(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect: VibrationEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrationEffect =
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
            vibrator.cancel()
            vibrator.vibrate(vibrationEffect)
        }
    }
}