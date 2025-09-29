package com.example.icebox

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "fridge_items",
    foreignKeys = [ForeignKey(
        entity = Ingredient::class,
        parentColumns = ["id"],
        childColumns = ["ingredientId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FridgeItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ingredientId: Int, // Ingredient의 id
    val quantity: Int,     // 수량
    val expiryDate: String // 유통기한, 예: "2025-06-11"
)
