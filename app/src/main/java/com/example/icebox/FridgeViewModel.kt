package com.example.icebox

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import android.util.Log

class FridgeViewModel(private val fridgeDao: FridgeDao) : ViewModel() {

    // 전체 목록 조회
    fun getFridgeItems(): LiveData<List<FridgeItemWithName>> = liveData {
        val data = fridgeDao.getAllFridgeItemsWithName()
        Log.d("FridgeViewModel", "전체 재료: $data")
        emit(data)
    }

    // 카테고리 필터링 포함
    fun getFilteredItems(category: String): LiveData<List<FridgeItemWithName>> = liveData {
        val allItems = fridgeDao.getAllFridgeItemsWithName()
        val filtered = if (category == "전체") allItems else allItems.filter { it.category == category }
        Log.d("FridgeViewModel", "필터링된 재료($category): $filtered")
        emit(filtered)
    }
}
