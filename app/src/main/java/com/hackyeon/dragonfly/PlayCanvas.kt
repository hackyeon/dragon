package com.hackyeon.dragonfly

import android.content.Context
import android.graphics.*
import android.view.View

class PlayCanvas(context: Context) : View(context) {

    private var paint = Paint()
    private var bitPlayerArr: Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.player_one),
        BitmapFactory.decodeResource(resources, R.drawable.player_two),
        BitmapFactory.decodeResource(resources, R.drawable.player_three),
        BitmapFactory.decodeResource(resources, R.drawable.player_death)
    )
    private var bitMonsterArr: Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.moster_one),
        BitmapFactory.decodeResource(resources, R.drawable.monster_two),
        BitmapFactory.decodeResource(resources, R.drawable.monster_three),
        BitmapFactory.decodeResource(resources, R.drawable.monster_four)
    )
    var bitStageArr: Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.map_one),
        BitmapFactory.decodeResource(resources, R.drawable.map_two)
    )

    private var bitShotArr: Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.shot_one),
        BitmapFactory.decodeResource(resources, R.drawable.shot_two),
        BitmapFactory.decodeResource(resources, R.drawable.shot_three)
    )
    private var bitBossArr: Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.boss_one),
        BitmapFactory.decodeResource(resources, R.drawable.boss_two),
    )

    private var bitBomb: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bomb)

    var player = mutableListOf<Player>()
//    var player = Player(width / 2, height + 150, 150, 0)
    var bulletList = mutableListOf<Item>()
    var enemyList = mutableListOf<Enemy>()
    var bombList = mutableListOf<Item>()
    var bossList = mutableListOf<Boss>()
    var locate = 0
    var stageCount = 0

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 배경 그리기
        canvas?.drawBitmap(
            bitStageArr[stageCount],
            Rect(0, 0, bitStageArr[stageCount].width, bitStageArr[stageCount].height),
            Rect(0, locate, width, height + locate),
            paint
        )
        canvas?.drawBitmap(
            bitStageArr[stageCount],
            Rect(0, 0, bitStageArr[stageCount].width, bitStageArr[stageCount].height),
            Rect(0, locate - height, width, locate),
            paint
        )

        //플레이어 그리기
        for(i in player){
            canvas?.drawBitmap(
                bitPlayerArr[i.type],
                Rect(0, 0, bitPlayerArr[i.type].width, bitPlayerArr[i.type].height),
                Rect(i.x - i.size, i.y - i.size, i.x + i.size, i.y + i.size),
                paint
            )
        }


//        canvas?.drawBitmap(
//            bitPlayerArr[test.type],
//            Rect(0, 0, bitPlayerArr[test.type].width, bitPlayerArr[test.type].height),
//            Rect(test.x - test.size, test.y- test.size, test.x + test.size, test.y+ test.size),
//            paint
//        )

        //총알 그리기
        synchronized(bulletList) {
            for (i in bulletList) {
                canvas?.drawBitmap(
                    bitShotArr[player[0].type],
                    Rect(0, 0, bitShotArr[player[0].type].width, bitShotArr[player[0].type].height),
                    Rect(i.x - i.size, i.y - i.size, i.x + i.size, i.y + i.size),
                    paint
                )
            }
        }

        //몬스터 그리기
        synchronized(enemyList) {
            for (i in enemyList) {
                var bit = bitMonsterArr[i.type]
                canvas?.drawBitmap(
                    bit,
                    Rect(0, 0, bit.width, bit.height),
                    Rect(i.x - i.size, i.y - i.size, i.x + i.size, i.y + i.size),
                    paint
                )
            }
        }

        // 보스 그리기
        synchronized(bossList) {
            for (i in bossList) {
                var bit = bitBossArr[stageCount]
                canvas?.drawBitmap(
                    bit,
                    Rect(0, 0, bit.width, bit.height),
                    Rect(i.x - i.size, i.y - i.size, i.x + i.size, i.y + i.size),
                    paint
                )
            }
        }

        //폭탄 그리기
        synchronized(bombList) {
            for (i in bombList) {
                canvas?.drawBitmap(
                    bitBomb,
                    Rect(0, 0, bitBomb.width, bitBomb.height),
                    Rect(i.x - i.size, i.y - i.size, i.x + i.size, i.y + i.size),
                    paint
                )
            }
        }
    }

    fun moveMap() {
        locate += 5
        if (locate >= height) locate = 0
    }

}