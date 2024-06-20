package com.example.cheesechase

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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
    private val _state = mutableStateOf(GameStates())
    val state: State<GameStates> = _state
    
    private val _jerry = mutableStateOf(Jerry())
    val jerry: State<Jerry> = _jerry

    private val _tom = mutableStateOf(Tom())
    val tom: State<Tom> = _tom

    private val _powerup = mutableStateOf(Powerup())
    val powerup: State<Powerup> = _powerup

    fun gamePause(){
        _state.value = _state.value.copy(gamePause = !state.value.gamePause)
    }
    fun sendgamePause():Boolean{
        return state.value.gamePause
    }
    fun gameOverFunction(){
        if(jerry.value.jerryTouched>1){
            _state.value = _state.value.copy(gamePause = true, gameOver = true)
        }
    }
    fun playAgain(dimension: WindowInfo){
        _state.value = _state.value.copy(gamePause = false, gameOver = false , highscore = 0, cheeseScore = 0)
        _jerry.value = _jerry.value.copy(jerryTouched = 0)
        _powerup.value = _powerup.value.copy( heartTime = 0)
        changeTrack(GameStates.Track.Middle, dimension)
    }
    fun useCheese(){
        if(state.value.cheeseScore>0) {
            _state.value = _state.value.copy(gamePause = false, gameOver = false,cheeseScore = state.value.cheeseScore-1)
            _jerry.value = _jerry.value.copy(jerryTouched = 0)
        }
    }

    fun gyrobutton(){
        _state.value = _state.value.copy(gyroMode = !state.value.gyroMode, touchMode = !state.value.touchMode)
    }
    
    fun openingAnimation(dimension: WindowInfo) {
        _jerry.value = _jerry.value.copy(jerryPositiony = dimension.screenHeightinDp - 275.dp,)
        _tom.value = _tom.value.copy(tomPositiony = dimension.screenHeightinDp - 150.dp)
        if (jerry.value.jerryTouched == 0 && state.value.highscore > 0) {
            _tom.value = _tom.value.copy(tomPositionyTiming = 2000, tomPositiony = 1000.dp)
        }
    }

    fun closeToJerry(dimension: WindowInfo) {
        if (jerry.value.jerryTouched == 1) {
            _tom.value = _tom.value.copy(
                tomPositionyTiming = 200,
                tomPositiony = dimension.screenHeightinDp - 150.dp
            )
        }
        if (jerry.value.jerryTouched > 1) {
            _tom.value = _tom.value.copy(
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
                    changeTrack(GameStates.Track.Middle, dimension)
                }

                in (dimension.screenWidth / 4 - dimension.screenWidth / 10 - 50F)..dimension.screenWidth / 4 + dimension.screenWidth / 10 - 50F -> {
                    changeTrack(GameStates.Track.Left, dimension)
                }

                in (3 * dimension.screenWidth / 4 - dimension.screenWidth / 10 + 50F)..3 * dimension.screenWidth / 4 + dimension.screenWidth / 10 + 50F -> {
                    changeTrack(GameStates.Track.Right, dimension)
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
                    var changeTrack: GameStates.Track
                    val Yaxis = gyroscope.getGyroscopeData().map { it.copy(y = it.y) }
                    Yaxis.collect {
                        //Gyro gives in rad/s . multiplied with time 1 sec and 100 just for multiplicative factor
                        phoneRotation += it.y * 100F
                        changeTrack = when {
                            phoneRotation > 700F -> {
                                GameStates.Track.Right
                            }

                            phoneRotation < (-500F) -> {
                                GameStates.Track.Left
                            }

                            else -> {
                                GameStates.Track.Middle
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
    private fun changeTrack(changeTrack: GameStates.Track, dimension: WindowInfo) {
        if (jerry.value.jerryTrack != changeTrack) {
            when (changeTrack) {
                GameStates.Track.Middle -> {
                    _jerry.value = _jerry.value.copy(
                        jerryPositionx = dimension.screenWidthinDp / 2 - 52.dp,
                        jerryTrack = changeTrack
                    )
                    _tom.value = _tom.value.copy(
                        tomPositionx = dimension.screenWidthinDp / 2 - 52.dp,
                    )
                }

                GameStates.Track.Left -> {
                    _jerry.value =_jerry.value.copy(
                        jerryPositionx = dimension.screenWidthinDp / 4 - 68.dp,
                        jerryTrack = changeTrack
                    )
                    _tom.value = _tom.value.copy(
                        tomPositionx = dimension.screenWidthinDp / 4 - 68.dp,
                    )
                }

                GameStates.Track.Right -> {
                    _jerry.value = _jerry.value.copy(
                        jerryPositionx = dimension.screenWidthinDp / 2 + 64.dp,
                        jerryTrack = changeTrack
                    )
                    _tom.value = _tom.value.copy(
                        tomPositionx = dimension.screenWidthinDp / 2 + 64.dp,
                    )
                }
            }
        }
    }

    //Obstacle Crossing Jerry's Y position
    fun obstacleCrossed(track: GameStates.Track, context: Context) {
        if (track == jerry.value.jerryTrack && !jerry.value.jerryJump &&!powerup.value.heart) {
            vibration(context)
            _jerry.value = _jerry.value.copy(jerryTouched = jerry.value.jerryTouched + 1)
        } else {
            _state.value = _state.value.copy(highscore = state.value.highscore + 1)
        }
    }

    //Auto Jump For Tom
    fun tomJump(track: GameStates.Track) {
        if (track == jerry.value.jerryTrack) {
            _tom.value = _tom.value.copy(tomJump = true)
        }
    }

    //Clickable Event for Jerry
    fun jumpJerry() {
        _jerry.value = _jerry.value.copy(jerryJump = true)
    }

    //Forced to keep these below 2 function separately coz LaunchedEffect needs composable function.
    @Composable
    fun JerryJumping(context: Context) {
        jump(context)
        LaunchedEffect(Unit) {
            _jerry.value = _jerry.value.copy(
                jerrySize = 160.dp,
                jerryPositionx = jerry.value.jerryPositionx - 25.dp
            )
            delay(200)
            _jerry.value = _jerry.value.copy(
                jerrySize = 110.dp,
                jerryPositionx = jerry.value.jerryPositionx + 25.dp,
                jerryJump = false
            )
        }
    }
    @Composable
    fun TomJumping() {
        LaunchedEffect(Unit) {
            _tom.value =
                _tom.value.copy(tomSize = 160.dp, tomPositionx = tom.value.tomPositionx - 25.dp)
            delay(200)
            _tom.value = _tom.value.copy(
                tomSize = 110.dp,
                tomPositionx = tom.value.tomPositionx + 25.dp,
                tomJump = false
            )
        }
    }

    private var previousMiddle= 0
    private var previousLeft= 0
    private var previousRight= 0
    private val list = (2000..8000 step 1500).toList()
    fun delay(track: GameStates.Track):Int{
        var random = list.random()
        return when(track){
            GameStates.Track.Middle ->{while(previousMiddle==random){random = list.random()} ; previousMiddle=random; random }
            GameStates.Track.Right ->{while(previousRight==random){random = list.random()} ; previousRight=random; random }
            GameStates.Track.Left ->{while(previousLeft==random){random = list.random()} ; previousLeft=random; random }
        }
    }

    fun heartJerryBool(track: GameStates.Track){
        if (track == jerry.value.jerryTrack && !jerry.value.jerryJump) {
            _powerup.value = _powerup.value.copy(heart = true, heartTime = 15)
        }
    }
    fun trapJerryBool(track: GameStates.Track){
        if (track == jerry.value.jerryTrack && !jerry.value.jerryJump) {
            _powerup.value = _powerup.value.copy(trap = true)
        }
    }
    fun cheeseJerryBool(track: GameStates.Track){
        if (track == jerry.value.jerryTrack && !jerry.value.jerryJump) {
            _powerup.value = _powerup.value.copy(cheese = true)
        }
    }

    @Composable
    fun HeartJerryTime(context: Context){
        if(powerup.value.heart) {
            LaunchedEffect(Unit) {
                powerup(context)
                while (powerup.value.heartTime > 0) {
                    if(!state.value.gamePause) {
                        delay(1000)
                        _powerup.value = _powerup.value.copy(heartTime = powerup.value.heartTime - 1)
                    }
                }
            }
            if (powerup.value.heartTime == 0) {
                _powerup.value = _powerup.value.copy(heart = false)
            }
        }
    }

    @Composable
    fun trapRandom(context: Context){
        if(powerup.value.trap) {
            LaunchedEffect(Unit) {
                powerup(context)
                val random = Random.nextBoolean()
                if (random) {
                    _jerry.value = _jerry.value.copy(jerryTouched = jerry.value.jerryTouched + 1)
                    delay(5000)
                    _powerup.value = _powerup.value.copy(trap = false)
                } else {

                        _powerup.value = _powerup.value.copy(speedReset = true)
                        delay(5000)
                        _powerup.value = _powerup.value.copy(speedReset = false, trap = false)

                }
            }
        }
    }
    fun speedReset():Boolean{
        return powerup.value.speedReset
    }

    @Composable
    fun CheeseCount(context: Context){
        if(powerup.value.cheese){
            LaunchedEffect(Unit){
                _state.value= _state.value.copy(cheeseScore = state.value.cheeseScore+1)
                powerup(context)
                delay(5000)
                _powerup.value = _powerup.value.copy(cheese = false)}
        }
    }

    private fun jump(context: Context){
        val mMediaPlayer = MediaPlayer.create(context, R.raw.jump)
        mMediaPlayer.start()
    }
    fun bg(context: Context){
        val mMediaPlayer = MediaPlayer.create(context, R.raw.background)
        mMediaPlayer.start()
    }
    private fun powerup(context: Context){
        val mMediaPlayer = MediaPlayer.create(context, R.raw.powerup)
        mMediaPlayer.start()
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