package com.example.fridgeapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.fridgeapp.data.AppDatabase
import com.example.fridgeapp.data.FridgeItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddIngredientActivity : AppCompatActivity() {
    private var selectedExpiryDate: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ingredient)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "fridge-database"
        ).fallbackToDestructiveMigration().build()

        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)
        val ingredientSpinner: Spinner = findViewById(R.id.ingredientSpinner)
        val quantityEditText: EditText = findViewById(R.id.quantityEditText)
        val expiryDateButton: Button = findViewById(R.id.expiryDateButton)
        val addButton: Button = findViewById(R.id.addButton)
        val clearFridgeButton: Button = findViewById(R.id.clearFridgeButton)
        val viewFridgeButton: Button = findViewById(R.id.viewFridgeButton)

        lifecycleScope.launch {
            val allIngredients = db.ingredientDao().getAll()
            val categories = allIngredients.map { it.category }.distinct()

            runOnUiThread {
                val categoryAdapter = ArrayAdapter(this@AddIngredientActivity, android.R.layout.simple_spinner_item, categories)
                categorySpinner.adapter = categoryAdapter

                categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                        val selectedCategory = categories[position]
                        val filtered = allIngredients.filter { it.category == selectedCategory }
                        val ingredientNames = filtered.map { it.name }
                        val ingredientAdapter = ArrayAdapter(this@AddIngredientActivity, android.R.layout.simple_spinner_item, ingredientNames)
                        ingredientSpinner.adapter = ingredientAdapter
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }

        expiryDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedExpiryDate = calendar.timeInMillis
                    val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
                    expiryDateButton.text = sdf.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        addButton.setOnClickListener {
            val selectedIngredientName = ingredientSpinner.selectedItem?.toString()
            val quantityStr = quantityEditText.text.toString()

            if (selectedIngredientName.isNullOrBlank() || quantityStr.isBlank()) {
                Toast.makeText(this, "입력을 모두 완료하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityStr.toIntOrNull()
            if (quantity == null) {
                Toast.makeText(this, "수량은 숫자만 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val ingredient = db.ingredientDao().findByName(selectedIngredientName)
                if (ingredient != null) {
                    val newItem = FridgeItem(
                        ingredientId = ingredient.id,
                        quantity = quantity,
                        addDate = System.currentTimeMillis(),
                        expiryDate = selectedExpiryDate
                    )
                    db.fridgeItemDao().insert(newItem)
                    Log.d("AddIngredient", "추가된 재료: $newItem")
                    runOnUiThread {
                        Toast.makeText(this@AddIngredientActivity, "재료 추가 완료", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }

        clearFridgeButton.setOnClickListener {
            lifecycleScope.launch {
                db.fridgeItemDao().deleteAll()
                runOnUiThread {
                    Toast.makeText(this@AddIngredientActivity, "냉장고 비움 완료", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewFridgeButton.setOnClickListener {
            startActivity(Intent(this, FridgeListActivity::class.java))
        }
    }
}
