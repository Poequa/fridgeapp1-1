package com.example.fridgeapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Ingredient::class, FridgeItem::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun fridgeItemDao(): FridgeItemDao
}
