package com.example.fridgeapp.data

import androidx.room.*

@Dao
interface FridgeItemDao {

    // 1. 냉장고에 재료 추가
    @Insert
    suspend fun insert(item: FridgeItem)

    // 2. 전체 삭제 (냉장고 비우기)
    @Query("DELETE FROM FridgeItem")
    suspend fun deleteAll()

    // 3. 개별 삭제 (FridgeItem 객체 직접 전달)
    @Delete
    suspend fun delete(item: FridgeItem)

    // 4. 개별 삭제 (id로 직접 삭제)
    @Query("DELETE FROM FridgeItem WHERE id = :id")
    suspend fun deleteById(id: Int)

    // 5. ingredientId 기준 재료 목록 조회 (등록일 순)
    @Query("SELECT * FROM FridgeItem WHERE ingredientId = :ingredientId ORDER BY addDate ASC")
    suspend fun getItemsByIngredientId(ingredientId: Int): List<FridgeItem>

    // 6. 냉장고 전체 목록 조회 + 식재료 이름 포함
    @Query("""
        SELECT FridgeItem.id, FridgeItem.ingredientId, Ingredient.name, FridgeItem.quantity, FridgeItem.addDate, FridgeItem.expiryDate
        FROM FridgeItem
        JOIN Ingredient ON FridgeItem.ingredientId = Ingredient.id
    """)
    suspend fun getAllWithIngredientNames(): List<FridgeItemWithName>

    // 7. 유통기한 임박 재료 이름만 조회 (레시피 추천용)
    @Query("""
        SELECT Ingredient.name
        FROM FridgeItem
        JOIN Ingredient ON FridgeItem.ingredientId = Ingredient.id
        WHERE FridgeItem.expiryDate IS NOT NULL AND FridgeItem.expiryDate <= :threshold
    """)
    suspend fun getExpiringIngredientNames(threshold: Long): List<String>
}
