package com.example.icebox

import androidx.room.Embedded

data class FridgeItemWithName(
    @Embedded val item: FridgeItem,
    val name: String,     // 재료 이름
    val category: String  // 카테고리
) {
    val id: Int get() = item.id
    val quantity: Int get() = item.quantity
    val expiryDate: String get() = item.expiryDate
}
