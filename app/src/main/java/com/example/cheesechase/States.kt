package com.example.cheesechase

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow

data class States(
    var highscore:Int =0,
    var cheeseScore:Int = 0,
    var speedReset: Boolean = false,

    var jerryJump:Boolean = false,
    var jerryTouched:Int =0,
    var jerryPositionx: Dp = 144.5.dp,
    var jerryPositiony: Dp = 1000.dp,
    var jerrySize:Dp=110.dp,
    var jerryTrack : Track = Track.Middle,

    var tomJump:Boolean = false,
    var tomSize:Dp=110.dp,
    var tomPositionx: Dp = 144.5.dp,
    var tomPositiony: Dp = 1300.dp,
    var tomPositionyTiming:Int = 2000,

    var showOthers: Boolean = true,
    var heart:Boolean = false,
    var trap:Boolean = false,
    var cheese:Boolean = false,
    var heartTime: Int = 0,

    var touchMode : Boolean = true,
    var gyroMode : Boolean = false
) {
    enum class Track {
        Left, Middle, Right
    }
}

data class GyroscopeData(
    val x: Float,
    val y: Float,
    val z: Float
)

interface Gyroscope {
    fun getGyroscopeData(): Flow<GyroscopeData>
}
