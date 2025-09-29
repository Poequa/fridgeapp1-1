package com.example.icebox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FridgeViewModelFactory(private val fridgeDao: FridgeDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 모델 클래스가 FridgeViewModel일 경우에만 생성
        if (modelClass.isAssignableFrom(FridgeViewModel::class.java)) {
            return FridgeViewModel(fridgeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
