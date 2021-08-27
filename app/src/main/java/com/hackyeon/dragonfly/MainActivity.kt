//package com.hackyeon.dragonfly
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.appcompat.widget.AppCompatImageButton
//import androidx.core.content.ContextCompat
//import com.hackyeon.dragonfly.databinding.ActivityMainBinding
//import kotlin.random.Random
//
//class MainActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var airplaneArr: Array<AppCompatImageButton>
//    private var airNumber: Int? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//        initView()
//        clickedButton()
//    }
//
//    private fun initView() {
//        airplaneArr = arrayOf(binding.airplaneOne, binding.airplaneTwo, binding.airplaneThree)
//    }
//
//    private fun clickedButton() {
//        binding.startButton.setOnClickListener {
//            if (airNumber == null) {
//                Toast.makeText(this, "비행기를 선택하세요.", Toast.LENGTH_SHORT).show()
//            } else {
//                var intent: Intent = Intent(this, GamePlayActivity::class.java)
//                intent.putExtra("airNumber", airNumber)
//                startActivity(intent)
//                finish()
//            }
//        }
//
//        for (i in airplaneArr.indices) {
//            airplaneArr[i].setOnClickListener {
//                if (airNumber != null) {
//                    airplaneArr[airNumber!!].background = null
//                }
//                airplaneArr[i].setBackgroundColor(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.airplane_background
//                    )
//                )
//                airNumber = i
//            }
//        }
//    }
//
//
//}