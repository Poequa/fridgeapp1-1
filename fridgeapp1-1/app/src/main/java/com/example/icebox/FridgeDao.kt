package com.example.icebox

import androidx.room.*

@Dao
interface FridgeDao {

    // ✅ 재료 테이블에 재료 추가 (앱 최초 실행 시)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient)

    // ✅ 냉장고에 재료 넣기 (중복 허용 - 처리 로직은 Activity에서)
    @Insert
    suspend fun insertFridgeItem(item: FridgeItem)

    // ✅ 기존 재료와 유통기한이 같은 항목 있는지 확인
    @Query("SELECT * FROM fridge_items WHERE ingredientId = :ingredientId AND expiryDate = :expiryDate LIMIT 1")
    suspend fun getFridgeItemByIngredientAndDate(ingredientId: Int, expiryDate: String): FridgeItem?

    // ✅ 기존 아이템 수량 업데이트용
    @Update
    suspend fun updateFridgeItem(item: FridgeItem)

    // ✅ 전체 재료 목록 불러오기
    @Query("SELECT * FROM ingredients")
    suspend fun getAllIngredients(): List<Ingredient>

    // ✅ 냉장고 재료 목록 (이름 포함)
    @Query("""
        SELECT fridge_items.*, ingredients.name, ingredients.category
        FROM fridge_items
        JOIN ingredients ON fridge_items.ingredientId = ingredients.id
    """)
    suspend fun getAllFridgeItemsWithName(): List<FridgeItemWithName>

    // ✅ 개별 재료 삭제
    @Delete
    suspend fun deleteFridgeItem(item: FridgeItem)

    // ✅ 냉장고 전체 비우기
    @Query("DELETE FROM fridge_items")
    suspend fun clearFridge()
}
