package com.example.fridgeapp.data

data class FridgeItemWithName(
    val id: Int,
    val ingredientId: Int,
    val name: String,
    val quantity: Int,
    val addDate: Long,
    val expiryDate: Long?
)
