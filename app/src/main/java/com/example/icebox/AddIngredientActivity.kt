package com.example.icebox

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import android.util.Log

class AddIngredientActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase  // ✅ DB 객체 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ingredient)

        // ✅ DB 연결
        db = AppDatabase.getDatabase(applicationContext)

        // 카테고리 버튼 클릭 시 세부 재료 토글
        findViewById<Button>(R.id.btnVegetables).setOnClickListener {
            toggleVisibility(R.id.layoutVegetables)
        }
        findViewById<Button>(R.id.btnMeat).setOnClickListener {
            toggleVisibility(R.id.layoutMeat)
        }
        findViewById<Button>(R.id.btnSauce).setOnClickListener {
            toggleVisibility(R.id.layoutSauce)
        }

        // 하위 재료 이미지 클릭 시 다이얼로그 표시
        findViewById<ImageView>(R.id.imgCarrot).setOnClickListener {
            showAddDialog("당근")
        }
        findViewById<ImageView>(R.id.imgLettuce).setOnClickListener {
            showAddDialog("상추")
        }
        findViewById<ImageView>(R.id.imgcabbage).setOnClickListener {
            showAddDialog("배추")
        }
        findViewById<ImageView>(R.id.imgpaprika).setOnClickListener {
            showAddDialog("파프리카")
        }
        findViewById<ImageView>(R.id.imgcucumber).setOnClickListener {
            showAddDialog("오이")
        }
        findViewById<ImageView>(R.id.imgPork).setOnClickListener {
            showAddDialog("돼지고기")
        }
        findViewById<ImageView>(R.id.imgChicken).setOnClickListener {
            showAddDialog("닭고기")
        }
        findViewById<ImageView>(R.id.imgBeef).setOnClickListener {
            showAddDialog("소고기")
        }
        findViewById<ImageView>(R.id.imgKetchup).setOnClickListener {
            showAddDialog("케찹")
        }
        findViewById<ImageView>(R.id.imgSoySauce).setOnClickListener {
            showAddDialog("간장")
        }

    }

    private fun toggleVisibility(layoutId: Int) {
        val layout = findViewById<LinearLayout>(layoutId)
        layout.visibility = if (layout.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    private fun showAddDialog(ingredientName: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_ingredient, null)
        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_quantity)
        val pickDateButton = dialogView.findViewById<Button>(R.id.btn_pick_date)
        val selectedDateText = dialogView.findViewById<TextView>(R.id.tv_selected_date)

        var selectedDate = ""

        pickDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this,
                { _, year, month, day ->
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                    selectedDateText.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        AlertDialog.Builder(this)
            .setTitle("$ingredientName 추가")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                val quantityText = quantityEditText.text.toString()
                val quantity = quantityText.toIntOrNull()

                if (quantity != null && selectedDate.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val ingredient = db.fridgeDao().getAllIngredients()
                            .find { it.name == ingredientName }

                        if (ingredient != null) {
                            // ✅ 중복 체크: 같은 재료 + 같은 날짜가 이미 있는지 확인
                            val existingItem = db.fridgeDao().getFridgeItemByIngredientAndDate(
                                ingredientId = ingredient.id,
                                expiryDate = selectedDate
                            )

                            if (existingItem != null) {
                                // ✅ 있으면 수량 업데이트
                                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                                db.fridgeDao().updateFridgeItem(updatedItem)

                                runOnUiThread {
                                    Toast.makeText(this@AddIngredientActivity, "${ingredient.name} 수량이 업데이트되었습니다!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // ✅ 없으면 새로 insert
                                val newItem = FridgeItem(
                                    ingredientId = ingredient.id,
                                    quantity = quantity,
                                    expiryDate = selectedDate
                                )
                                db.fridgeDao().insertFridgeItem(newItem)

                                runOnUiThread {
                                    Toast.makeText(this@AddIngredientActivity, "${ingredient.name} 저장 완료!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@AddIngredientActivity, "재료 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "수량과 날짜를 입력하세요!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
