package com.example.icebox

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class FridgeAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ingredient) // XML 이름 정확히 일치시킴

        // 채소 버튼 & 레이아웃
        val vegButton = findViewById<Button>(R.id.btnVegetables)
        val vegLayout = findViewById<LinearLayout>(R.id.layoutVegetables)

        // 육류 버튼 & 레이아웃
        val meatButton = findViewById<Button>(R.id.btnMeat)
        val meatLayout = findViewById<LinearLayout>(R.id.layoutMeat)

        // 소스 버튼 & 레이아웃
        val sauceButton = findViewById<Button>(R.id.btnSauce)
        val sauceLayout = findViewById<LinearLayout>(R.id.layoutSauce)

        // 채소 버튼 클릭 시 하위 재료 보이기/숨기기
        vegButton.setOnClickListener {
            vegLayout.visibility = if (vegLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // 육류 버튼 클릭 시 하위 재료 보이기/숨기기
        meatButton.setOnClickListener {
            meatLayout.visibility = if (meatLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // 소스 버튼 클릭 시 하위 재료 보이기/숨기기
        sauceButton.setOnClickListener {
            sauceLayout.visibility = if (sauceLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }
}
