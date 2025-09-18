package com.example.fridgeapp

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.fridgeapp.data.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FridgeListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fridge_list)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "fridge-database"
        ).fallbackToDestructiveMigration().build()

        val listView: ListView = findViewById(R.id.fridgeListView)

        lifecycleScope.launch {
            val items = db.fridgeItemDao().getAllWithIngredientNames()
            Log.d("FridgeList", "조회된 재료 수: ${items.size}")
            val sdf = SimpleDateFormat("yy.MM.dd", Locale.KOREA)

            val listText = items.map {
                val expiry = it.expiryDate?.let { d -> sdf.format(Date(d)) } ?: "없음"
                "${it.name} - ${it.quantity}개 (유통기한: $expiry)"
            }

            runOnUiThread {
                listView.adapter = ArrayAdapter(this@FridgeListActivity, android.R.layout.simple_list_item_1, listText)
            }
        }
    }
}
