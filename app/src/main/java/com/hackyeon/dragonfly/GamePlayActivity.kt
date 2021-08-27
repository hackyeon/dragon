package com.hackyeon.dragonfly

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import com.hackyeon.dragonfly.databinding.ActivityGamePlayBinding
import kotlin.random.Random

//todo player 리스트를 class 변수 하나로 만들기

class GamePlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGamePlayBinding
    private lateinit var canvas: PlayCanvas
    private lateinit var selectButtonArr: Array<AppCompatImageButton>
    private var selectNum: Int? = null
    private var isPlay = true
    private var isMonsterTime = true
    private var isBossTime = false
    private var isShot = true
    private var isMyShot = true
    private lateinit var player: MutableList<Player>
    private lateinit var bullet: MutableList<Item>
    private lateinit var enemy: MutableList<Enemy>
    private lateinit var bomb: MutableList<Item>
    private lateinit var boss: MutableList<Boss>
    private var killCount = 0

    private lateinit var test: Player

    private var handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0) {
                if (canvas.width > 0) {
                    PlayThread().start()
                } else {
                    sendEmptyMessageDelayed(0, 100)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamePlayBinding.inflate(layoutInflater)
        var view = binding.root
        canvas = PlayCanvas(this)
        setContentView(view)
        binding.canvas.addView(canvas)

        initView()
        handler.sendEmptyMessage(0)
        clickedButton()
        canvas.setOnTouchListener { _: View, event: MotionEvent ->
            // todo 여기도 수정 가능
            if (player.isNotEmpty()) player[0].move(event)
            true
        }
    }

    private fun initView() {
        player = canvas.player
        bullet = canvas.bulletList
        enemy = canvas.enemyList
        bomb = canvas.bombList
        boss = canvas.bossList
//        test = canvas.test
        selectButtonArr = arrayOf(binding.playerOne, binding.playerTwo, binding.playerThree)
    }

    private fun clickedButton() {
        for (i in selectButtonArr.indices) {
            selectButtonArr[i].setOnClickListener {
                //todo 캐릭터 선택 확인 기능
                selectNum = i
            }
        }

        binding.startButton.setOnClickListener {
            if (selectNum != null) {
                startGame()
            } else {
                Toast.makeText(this, "캐릭터를 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGame() {
        canvas.stageCount = 0
        isMonsterTime = true
        isShot = true
        isMyShot = true
        binding.titleTextView.visibility = GONE
        binding.startButton.visibility = GONE
        binding.selectLayout.visibility = GONE
        player.add(
            Player(
                canvas.width / 2,
                canvas.height + 150,
                150,
                selectNum!!
            )
        )
        player[0].startMove()
        MoveShotThread().start()
        EnemyThread().start()
        CrashThread().start()
    }


    inner class PlayThread : Thread() {
        override fun run() {
            super.run()
            while (isPlay) {
                runOnUiThread {
                    canvas.invalidate()
                }
                canvas.moveMap()
                sleep(20) // 50프레임
            }
        }
    }

    inner class MoveShotThread : Thread() {
        override fun run() {
            super.run()
            while (isShot) {
                if (isMyShot) {
                    // 플레이어 총알 생성
                    player[0].fire(canvas)

                    // 플레이어 총알 이동
                    synchronized(bullet) {
                        for (i in bullet) {
                            i.move(-50)
                        }
                    }
                    // 플레이어 총알 제거
                    var deleteBullet = 0
                    synchronized(bullet) {
                        while (deleteBullet < bullet.size) {
                            if (bullet[deleteBullet].y + bullet[deleteBullet].size < 0) {
                                bullet.removeAt(deleteBullet)
                                deleteBullet--
                            }
                            deleteBullet++
                        }
                    }
                }

                // 몬스터 총알 이동
                synchronized(bomb) {
                    for (i in bomb) {
                        i.enemyMove(canvas)
                    }
                }
                // 몬스터 총알 제거
                var deleteBomb = 0
                synchronized(bomb) {
                    while (deleteBomb < bomb.size) {
                        // todo x값 삭제 기능 추가
                        if (bomb[deleteBomb].y - bomb[deleteBomb].size > canvas.height) {
                            bomb.removeAt(deleteBomb)
                            deleteBomb--
                        }
                        deleteBomb++
                    }
                }
                sleep(50)
            }
        }
    }

    inner class EnemyThread : Thread() {
        override fun run() {
            super.run()
            // 몬스터 생성
            var create = 0
            while (isMonsterTime) {
                // todo 생성주기 수정하기
                if (create >= 10) {
                    var type = Random.nextInt(0, 4)
//                    var y = Random.nextInt(0, canvas.height / 3)
//                    var x = if (y == 0) {
//                        Random.nextInt(0, canvas.width)
//                    } else {
//                        if (Random.nextInt(0, 2) == 0) 0 else canvas.width
//                    }
//                    var speedY = Random.nextInt(30, 50)
//                    var speedX = if (x >= canvas.width / 2) speedY * -1 else speedY
                    var x: Int = 0
                    var y: Int = 0
                    var speedX: Int = 0
                    var speedY: Int = 0
                    // todo 캐릭터 사이즈 150 companion object 만들기

                    when (type) {
                        0 -> {
                            x = if (Random.nextInt(0, 2) == 0) -150 else canvas.width + 150
                            y = Random.nextInt(200, canvas.height / 2)
                            speedX = if (Random.nextInt(0, 2) == 0) Random.nextInt(
                                30,
                                50
                            ) else (Random.nextInt(30, 50)) * -1
                        }
                        1 -> {
                            x = Random.nextInt(150, canvas.width - 150)
                            y = -150
                            speedY = Random.nextInt(30, 50)
                        }
                        2 -> {
                            x = if (Random.nextInt(0, 2) == 0) -150 else canvas.width + 150
                            y = -150
                            speedX = Random.nextInt(30, 50)
                            speedY = Random.nextInt(30, 50)
                        }
                        else -> {
                            x = Random.nextInt(150, canvas.width - 150)
                            y = Random.nextInt(150, canvas.height / 3)
                        }
                    }

                    synchronized(enemy) {
                        enemy.add(
                            Enemy(x, y, 150, type, speedX, speedY)
                        )
                    }
                    create = 0
                } else {
                    create++
                }

                // 몬스터이동, 총알발사, 턴
                synchronized(enemy) {
                    for (i in enemy) {
                        i.moveEnemy(canvas)
                        i.turn(canvas)
                        i.fire(canvas)
                    }
                }

                // 몬스터 제거 ( 화면밖으로 나갈경우)
                var deleteCnt = 0
                synchronized(enemy) {
                    while (deleteCnt < enemy.size) {
                        if (enemy[deleteCnt].x - enemy[deleteCnt].size > canvas.width || enemy[deleteCnt].x + enemy[deleteCnt].size < 0) {
                            enemy.removeAt(deleteCnt)
                            deleteCnt--
                        }
                        deleteCnt++
                    }
                }
                sleep(50)
            }
        }
    }

    inner class BossThread : Thread() {
        override fun run() {
            super.run()
            while (isBossTime) {
                boss[0].fire(canvas)
                sleep(50)
            }
        }
    }

    inner class CrashThread : Thread() {
        override fun run() {
            super.run()
            while (isPlay) {

                // 적-총알 충돌 시작
                var enemyIndex = 0
                synchronized(enemy) {
                    while (enemyIndex < enemy.size) {
                        synchronized(bullet) {
                            for (bulletIndex in bullet.indices) {
                                var enemyRect: Rect = Rect(
                                    enemy[enemyIndex].x - (enemy[enemyIndex].size / 2),
                                    enemy[enemyIndex].y - (enemy[enemyIndex].size / 2),
                                    enemy[enemyIndex].x + (enemy[enemyIndex].size / 2),
                                    enemy[enemyIndex].y + (enemy[enemyIndex].size / 2)
                                )
                                var bulletRect: Rect = Rect(
                                    bullet[bulletIndex].x - (bullet[bulletIndex].size / 2),
                                    bullet[bulletIndex].y - (bullet[bulletIndex].size / 2),
                                    bullet[bulletIndex].x + (bullet[bulletIndex].size / 2),
                                    bullet[bulletIndex].y + (bullet[bulletIndex].size / 2)
                                )
                                if (enemyRect.intersect(bulletRect)) {
                                    enemy.removeAt(enemyIndex)
                                    bullet.removeAt(bulletIndex)
                                    enemyIndex--
                                    killCount++
                                    break
                                }
                            }
                        }
                        enemyIndex++
                    }
                }
                // 적-총알 충돌 끝

                // 보스 생성 시작
                if (killCount > (20 * (canvas.stageCount+1)) && isMonsterTime) {
                    killCount = 0
                    synchronized(enemy) { enemy.clear() }
                    synchronized(bomb) { bomb.clear() }
                    synchronized(bullet) { bullet.clear() }
                    isMonsterTime = false
                    isBossTime = true
                    boss.add(
                        Boss(
                            canvas.width / 2,
                            -300,
                            300,
                            canvas.stageCount,
                            10 * (canvas.stageCount + 1) // 기존값 20
                        )
                    )
                    boss[0].startMove()
                    BossThread().start()
                }
                // 보스 생성 끝


                // 플레이어 - 폭탄 충돌 시작
                synchronized(bomb) {
                    for (i in bomb) {
                        var bombRect: Rect = Rect(
                            i.x - (i.size / 2),
                            i.y - (i.size / 3),
                            i.x + (i.size / 2),
                            i.y + (i.size / 3)
                        )
                        var playerRect: Rect = Rect(
                            player[0].x - (player[0].size / 3),
                            player[0].y - (player[0].size / 3),
                            player[0].x + (player[0].size / 3),
                            player[0].y + (player[0].size / 3)
                        )
                        if (bombRect.intersect(playerRect)) {
                            isMyShot = false
                            synchronized(bullet) { bullet.clear() }
                            player[0].deathMove(canvas)
                            restartThread().start()
                        }
                    }
                }
                // 플레이어 - 폭탄 충돌 끝

                // 보스 - 총알 충돌 시작
                if (boss.isNotEmpty()) {
                    var bulletIndex = 0
                    synchronized(bullet) {
                        while (bulletIndex < bullet.size) {
                            var bossRect: Rect = Rect(
                                boss[0].x - (boss[0].size / 2),
                                boss[0].y - (boss[0].size / 2),
                                boss[0].x + (boss[0].size / 2),
                                boss[0].y + (boss[0].size / 2)
                            )
                            var bulletRect: Rect = Rect(
                                bullet[bulletIndex].x - (bullet[bulletIndex].size / 2),
                                bullet[bulletIndex].y - (bullet[bulletIndex].size / 2),
                                bullet[bulletIndex].x + (bullet[bulletIndex].size / 2),
                                bullet[bulletIndex].y + (bullet[bulletIndex].size / 2)
                            )
                            if (bulletRect.intersect(bossRect) && boss[0].hp > 0) {
                                bullet.removeAt(bulletIndex)
                                boss[0].hp--
                                bulletIndex--
                            } else if (bulletRect.intersect(bossRect) && boss[0].hp == 0) {
                                bullet.removeAt(bulletIndex)
                                synchronized(boss){ boss.removeAt(0) }
                                player[0].endMove(canvas)
                                synchronized(bomb) { bomb.clear() }
                                synchronized(bullet) { bullet.clear() }
                                isBossTime = false
                                isShot = false
                                if (canvas.stageCount == 0) nextStageThread().start() else restartThread().start()
                                break
                            }
                            bulletIndex++
                        }
                    }
                }
                // 보스 - 총알 충돌 끝


                sleep(1)
            }
        }
    }

    inner class nextStageThread : Thread() {
        override fun run() {
            super.run()
            while (!isBossTime && !isShot && !isMonsterTime) {
                if (player[0].y in canvas.height - 200..canvas.height) {
                    killCount = 0
                    isMonsterTime = true
                    isShot = true
                    MoveShotThread().start()
                    EnemyThread().start()
                    break
                }
                sleep(50)
            }
        }
    }

    inner class restartThread() : Thread() {
        override fun run() {
            super.run()
            while (true) {
                if (player.isNotEmpty() && player[0].y >= canvas.height + player[0].size) {
                    killCount = 0
                    isMonsterTime = false
                    isBossTime = false
                    isShot = false
                    selectNum = null
                    synchronized(bomb) { bomb.clear() }
                    bullet.clear()
                    player.clear()
                    boss.clear()
                    enemy.clear()
                    runOnUiThread {
                        binding.titleTextView.visibility = VISIBLE
                        binding.selectLayout.visibility = VISIBLE
                        binding.startButton.visibility = VISIBLE
                    }
                    break
                }
                sleep(50)
            }
        }
    }


}