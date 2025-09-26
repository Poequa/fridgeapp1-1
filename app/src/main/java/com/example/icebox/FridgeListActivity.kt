package com.example.icebox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.icebox.databinding.ActivityFridgeListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.text.Editable
import android.text.TextWatcher

class FridgeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFridgeListBinding
    private lateinit var adapter: FridgeAdapter
    private var allItems: List<FridgeItemWithName> = listOf()

    private val fridgeDao: FridgeDao by lazy {
        AppDatabase.getDatabase(applicationContext).fridgeDao()
    }

    private val fridgeViewModel: FridgeViewModel by viewModels {
        FridgeViewModelFactory(fridgeDao)
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // ✅ 필터 상태 저장용 변수
    private var selectedCategory = "전체"
    private var selectedSort = "기본"
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFridgeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = FridgeAdapter(mutableListOf()) { selectedItem ->
            showDeleteDialog(selectedItem)
        }

        binding.recyclerViewFridge.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFridge.adapter = adapter

        val categories = listOf("전체", "채소", "육류", "소스")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spinnerCategory.adapter = categoryAdapter

        val sortOptions = listOf("기본", "유통기한 빠른 순", "수량 많은 순")
        val sortAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions)
        binding.spinnerSort.adapter = sortAdapter

        // ✅ + 버튼 클릭 시 AddIngredientActivity로 이동
        binding.fabAddIngredient.setOnClickListener {
            val intent = Intent(this, AddIngredientActivity::class.java)
            startActivity(intent)
        }

        // ✅ 검색창 텍스트 변경 리스너
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                applyFilterAndSort()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        fridgeViewModel.getFridgeItems().observe(this) { items ->
            Log.d("FridgeListActivity", "observe 됨! 개수: ${items.size}")
            allItems = items
            applyFilterAndSort()
        }

        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = parent.getItemAtPosition(position).toString()
                applyFilterAndSort()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSort = parent.getItemAtPosition(position).toString()
                applyFilterAndSort()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun applyFilterAndSort() {
        var filtered = allItems

        // ✅ 카테고리 필터
        if (selectedCategory != "전체") {
            filtered = filtered.filter { it.category == selectedCategory }
        }

        // ✅ 검색 필터
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }

        // ✅ 정렬
        filtered = when (selectedSort) {
            "유통기한 빠른 순" -> filtered.sortedBy {
                LocalDate.parse(it.item.expiryDate, dateFormatter)
            }
            "수량 많은 순" -> filtered.sortedByDescending { it.item.quantity }
            else -> filtered
        }

        adapter.updateItems(filtered.toMutableList())

        // ✅ 결과 없을 경우 안내 문구
        binding.textEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showDeleteDialog(item: FridgeItemWithName) {
        AlertDialog.Builder(this)
            .setTitle("${item.name} 삭제")
            .setMessage("정말로 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    fridgeDao.deleteFridgeItem(item.item)
                    val updatedItems = fridgeDao.getAllFridgeItemsWithName()
                    runOnUiThread {
                        allItems = updatedItems
                        applyFilterAndSort()
                    }
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
