package com.example.fridgeapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IngredientDao {

    // 모든 재료 목록 불러오기 (스피너용)
    @Query("SELECT * FROM Ingredient")
    suspend fun getAll(): List<Ingredient>

    // 이름으로 재료 조회 (재료 추가 시 사용)
    @Query("SELECT * FROM Ingredient WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): Ingredient

    // 재료 추가 (필요 시)
    @Insert
    suspend fun insert(ingredient: Ingredient)
}
