package com.hackyeon.dragonfly

import android.util.Log
import android.view.MotionEvent
import kotlin.random.Random

open class Item(
    var x: Int,
    var y: Int,
    var size: Int
){
    private var speedX = Random.nextInt(-30, 30)
    private var speedY = 30

    fun move(speed: Int){
        y += speed
    }

    fun enemyMove(canvas: PlayCanvas){
        if(x - size < 0 || x + size > canvas.width) speedX *= -1
        x += speedX
        y += speedY
    }
}