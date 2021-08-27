package com.hackyeon.dragonfly

import kotlin.random.Random

class Enemy(
    x: Int,
    y: Int,
    size: Int,
    type: Int,
    var speedX: Int,
    var speedY: Int
) : Player(x, y, size, type) {
    private var turnCount: Int = 0
    private var bombCount: Int = 0
    private var limitY: Int = Random.nextInt(2, 4)
    private var checkMoveY = false

    fun moveEnemy(canvas: PlayCanvas) {
        when (type) {
            0 -> x += speedX
            1 -> if (y <= canvas.height / limitY) y += speedY else checkMoveY = true
            2 -> {
                x += speedX
                y += speedY
            }
        }
    }

    fun turn(canvas: PlayCanvas) {
        if (type == 2) {
            if (y > canvas.height / 1.5) {
                speedY *= -1
            }
            if (turnCount == 50) {
                speedX *= -1
            }
            turnCount += Random.nextInt(1, 5)
        }
    }

    override fun fire(canvas: PlayCanvas) {
        if (bombCount >= 10) {
            synchronized(canvas.bombList) { canvas.bombList.add(Item(x, y + size, 40)) }
            bombCount = 0
        }
        bombCount += when (type) {
            0 -> 1
            1 -> if(checkMoveY) 2 else 0
            2 -> 1
            else -> 1
        }

    }
}