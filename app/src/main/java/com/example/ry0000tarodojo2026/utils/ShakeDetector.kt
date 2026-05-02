package com.example.ry0000tarodojo2026.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    // シェイクを判定する加速度のしきい値（後で微調整）
    private val SHAKE_THRESHOLD_GRAVITY = 2.0f
    
    // 連続でカウントされるのを防ぐためのクールダウン（ミリ秒）
    private val SHAKE_SLOP_TIME_MS = 500
    private var shakeTimestamp: Long = 0

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // 重力加速度(9.8)を1.0としたときのGフォースを計算
            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            // ベクトルの大きさを計算
            val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

            // しきい値を超えた場合
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                // クールダウン時間を過ぎているかチェック
                if (now - shakeTimestamp > SHAKE_SLOP_TIME_MS) {
                    shakeTimestamp = now
                    onShake() // シェイク検知コールバックを実行
                }
            }
        }
    }
}
