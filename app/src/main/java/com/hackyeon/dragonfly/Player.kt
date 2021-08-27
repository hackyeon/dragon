package com.hackyeon.dragonfly

import android.util.Log
import android.view.MotionEvent
import java.lang.Thread.sleep
import kotlin.random.Random

open class Player(
    x: Int,
    y: Int,
    size: Int,
    var type: Int
) : Item(x, y, size) {
    private var clickCharacter = false
    private var bulletCount = 0

    fun move(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x.toInt() in x - size..x + size && event.y.toInt() in y - size..y + size) clickCharacter =
                    true
            }
            MotionEvent.ACTION_MOVE -> {
                if (clickCharacter) x = event.x.toInt()
            }
            MotionEvent.ACTION_UP -> {
                clickCharacter = false
            }
        }
    }

    open fun fire(canvas: PlayCanvas) {
        if (bulletCount == 2) {
            synchronized(canvas.bulletList) { canvas.bulletList.add(Item(x, y - size, 40)) }
            bulletCount = 0
        } else {
            bulletCount++
        }
    }

    open fun startMove() {
        Thread {
            for (i in 0 until 10) {
                y -= (size + 300) / 10
                sleep(20)
            }
        }.start()
    }

    fun endMove(canvas: PlayCanvas) {
        Thread {
            while (y + size >= 0) {
                y -= 50
                if (y + size < 0) {
                    if (canvas.stageCount < 1) {
                        canvas.stageCount++
                        x = canvas.width / 2
                        y = canvas.height + size
                        startMove()
                    } else {
                        x = canvas.width / 2
                        y = canvas.height + size
                    }
                    break
                }
                sleep(20)
            }
        }.start()
    }

    fun deathMove(canvas: PlayCanvas) {
        type = 3
        Thread{
            while (y-size < canvas.height) {
                y += 50
                sleep(50)
            }
        }.start()

    }

}