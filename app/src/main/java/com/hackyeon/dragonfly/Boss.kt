package com.hackyeon.dragonfly

import kotlin.random.Random

class Boss(
    x: Int,
    y: Int,
    size: Int,
    type: Int,
    var hp: Int
): Player(x, y, size, type) {
    private var isMove = true
    private var bombCount = 0

    override fun startMove(){
        Thread{
            for(i in 0 until 10){
                y += (size + 400)/10
                Thread.sleep(20)
            }
            isMove = false
        }.start()
    }

    override fun fire(canvas: PlayCanvas) {
        if(!isMove){
            if(bombCount >= 8){
                synchronized(canvas.bombList){
                    canvas.bombList.add(Item(Random.nextInt(x-size, x+size), y+size, 60))
                }
                bombCount = 0
            }else {
                bombCount += Random.nextInt(1, 9)
            }

        }
    }
}