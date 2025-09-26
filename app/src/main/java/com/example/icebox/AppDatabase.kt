package com.example.icebox

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Ingredient::class, FridgeItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fridgeDao(): FridgeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fridge-db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getDatabase(context).fridgeDao().apply {
                                    insertIngredient(Ingredient(name = "토마토", category = "채소"))
                                    insertIngredient(Ingredient(name = "당근", category = "채소"))
                                    insertIngredient(Ingredient(name = "상추", category = "채소"))
                                    insertIngredient(Ingredient(name = "배추", category = "채소"))
                                    insertIngredient(Ingredient(name = "오이", category = "채소"))
                                    insertIngredient(Ingredient(name = "파프리카", category = "채소"))
                                    insertIngredient(Ingredient(name = "양파", category = "채소"))
                                    insertIngredient(Ingredient(name = "마늘", category = "채소"))
                                    insertIngredient(Ingredient(name = "고추", category = "채소"))
                                    insertIngredient(Ingredient(name = "브로콜리", category = "채소"))
                                    insertIngredient(Ingredient(name = "표고버섯", category = "채소"))
                                    insertIngredient(Ingredient(name = "팽이버섯", category = "채소"))
                                    insertIngredient(Ingredient(name = "송이버섯", category = "채소"))
                                    insertIngredient(Ingredient(name = "양송이버섯", category = "채소"))
                                    insertIngredient(Ingredient(name = "새송이버섯", category = "채소"))
                                    insertIngredient(Ingredient(name = "느타리버섯", category = "채소"))
                                    insertIngredient(Ingredient(name = "목이버섯", category = "채소"))
                                    insertIngredient(Ingredient(name = "트러플버섯", category = "채소"))
                                    insertIngredient(Ingredient(name = "애호박", category = "채소"))
                                    insertIngredient(Ingredient(name = "무", category = "채소"))
                                    insertIngredient(Ingredient(name = "부추", category = "채소"))
                                    insertIngredient(Ingredient(name = "양배추", category = "채소"))
                                    insertIngredient(Ingredient(name = "감자", category = "채소"))
                                    insertIngredient(Ingredient(name = "고구마", category = "채소"))
                                    insertIngredient(Ingredient(name = "대파", category = "채소"))
                                    insertIngredient(Ingredient(name = "쪽파", category = "채소"))
                                    insertIngredient(Ingredient(name = "시금치", category = "채소"))
                                    insertIngredient(Ingredient(name = "청경채", category = "채소"))
                                    insertIngredient(Ingredient(name = "가지", category = "채소"))
                                    insertIngredient(Ingredient(name = "옥수수", category = "채소"))
                                    insertIngredient(Ingredient(name = "콩나물", category = "채소"))
                                    insertIngredient(Ingredient(name = "숙주", category = "채소"))

                                    insertIngredient(Ingredient(name = "소고기", category = "육류"))
                                    insertIngredient(Ingredient(name = "돼지고기", category = "육류"))
                                    insertIngredient(Ingredient(name = "닭고기", category = "육류"))
                                    insertIngredient(Ingredient(name = "베이컨", category = "육류"))
                                    insertIngredient(Ingredient(name = "햄", category = "육류"))
                                    insertIngredient(Ingredient(name = "스팸", category = "육류"))
                                    insertIngredient(Ingredient(name = "계란", category = "육류"))
                                    insertIngredient(Ingredient(name = "오리고기", category = "육류"))
                                    insertIngredient(Ingredient(name = "훈제오리", category = "육류"))
                                    insertIngredient(Ingredient(name = "닭가슴살", category = "육류"))
                                    insertIngredient(Ingredient(name = "소시지", category = "육류"))
                                    insertIngredient(Ingredient(name = "메추리알", category = "육류"))
                                    insertIngredient(Ingredient(name = "닭날개", category = "육류"))

                                    insertIngredient(Ingredient(name = "고등어", category = "해산물"))
                                    insertIngredient(Ingredient(name = "명태", category = "해산물"))
                                    insertIngredient(Ingredient(name = "오징어", category = "해산물"))
                                    insertIngredient(Ingredient(name = "새우", category = "해산물"))
                                    insertIngredient(Ingredient(name = "게", category = "해산물"))
                                    insertIngredient(Ingredient(name = "연어", category = "해산물"))
                                    insertIngredient(Ingredient(name = "참치", category = "해산물"))
                                    insertIngredient(Ingredient(name = "바지락", category = "해산물"))
                                    insertIngredient(Ingredient(name = "홍합", category = "해산물"))
                                    insertIngredient(Ingredient(name = "쭈꾸미", category = "해산물"))
                                    insertIngredient(Ingredient(name = "낙지", category = "해산물"))
                                    insertIngredient(Ingredient(name = "가리비", category = "해산물"))

                                    insertIngredient(Ingredient(name = "케찹", category = "소스"))
                                    insertIngredient(Ingredient(name = "마요네즈", category = "소스"))
                                    insertIngredient(Ingredient(name = "간장", category = "소스"))
                                    insertIngredient(Ingredient(name = "고추장", category = "소스"))
                                    insertIngredient(Ingredient(name = "된장", category = "소스"))
                                    insertIngredient(Ingredient(name = "참기름", category = "소스"))
                                    insertIngredient(Ingredient(name = "들기름", category = "소스"))
                                    insertIngredient(Ingredient(name = "버터", category = "소스"))
                                    insertIngredient(Ingredient(name = "고춧가루", category = "소스"))
                                    insertIngredient(Ingredient(name = "굴소스", category = "소스"))
                                    insertIngredient(Ingredient(name = "카레가루", category = "소스"))
                                    insertIngredient(Ingredient(name = "머스터드", category = "소스"))
                                    insertIngredient(Ingredient(name = "올리브유", category = "소스"))

                                    insertIngredient(Ingredient(name = "두부", category = "기타"))
                                    insertIngredient(Ingredient(name = "김치", category = "기타"))
                                    insertIngredient(Ingredient(name = "우유", category = "기타"))
                                    insertIngredient(Ingredient(name = "요거트", category = "기타"))
                                    insertIngredient(Ingredient(name = "치즈", category = "기타"))
                                    insertIngredient(Ingredient(name = "쌈장", category = "기타"))
                                    insertIngredient(Ingredient(name = "우동사리", category = "기타"))
                                    insertIngredient(Ingredient(name = "식빵", category = "기타"))
                                    insertIngredient(Ingredient(name = "또띠야", category = "기타"))
                                    insertIngredient(Ingredient(name = "냉동만두", category = "기타"))


                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}