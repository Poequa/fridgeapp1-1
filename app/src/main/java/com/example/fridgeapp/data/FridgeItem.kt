package com.example.fridgeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FridgeItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ingredientId: Int,
    val quantity: Int,
    val addDate: Long,
    val expiryDate: Long?
)
