package com.example.icebox

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IngredientDao {

    // 재료 하나 추가 (같은 이름이면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredient: Ingredient)

    // 이름으로 재료 1개 조회 (예: "당근")
    @Query("SELECT * FROM ingredients WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): Ingredient?
}
