package com.example.fridgeapp.data // ← 너의 프로젝트 패키지에 맞춰서

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,         // 식재료 이름 (예: 양파, 돼지고기)
    val category: String      // 카테고리 (예: 채소, 육류)
)
