package com.example.icebox

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import android.util.Log

class AddIngredientActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase  // ✅ DB 객체 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ingredient)

        // ✅ DB 연결
        db = AppDatabase.getDatabase(applicationContext)

        // 카테고리 버튼 클릭 시 세부 재료 토글
        listOf(
            R.id.btnVegetables to R.id.layoutVegetables,
            R.id.btnMeat to R.id.layoutMeat,
            R.id.btnSeafood to R.id.layoutSeafood,
            R.id.btnSauce to R.id.layoutSauce,
            R.id.btnOthers to R.id.layoutOthers
        ).forEach { (buttonId, layoutId) ->
            findViewById<Button>(buttonId).setOnClickListener {
                toggleVisibility(layoutId)
            }
        }

        // 하위 재료 이미지 클릭 시 다이얼로그 표시
        val ingredientClickMap = mapOf(
            R.id.imgTomato to "토마토",
            R.id.imgCarrot to "당근",
            R.id.imgLettuce to "상추",
            R.id.imgBaechu to "배추",
            R.id.imgCucumber to "오이",
            R.id.imgPaprika to "파프리카",
            R.id.imgOnion to "양파",
            R.id.imgGarlic to "마늘",
            R.id.imgPepper to "고추",
            R.id.imgBroccoli to "브로콜리",
            R.id.imgShiitakemushroom to "표고버섯",
            R.id.imgEnokimushroom to "팽이버섯",
            R.id.imgMatsutakemushroom to "송이버섯",
            R.id.imgButtonmushroom to "양송이버섯",
            R.id.imgKingoystermushroom to "새송이버섯",
            R.id.imgOystermushroom to "느타리버섯",
            R.id.imgWoodearmushroom to "목이버섯",
            R.id.imgTrufflemushroom to "트러플버섯",
            R.id.imgZucchini to "애호박",
            R.id.imgRadish to "무",
            R.id.imgChive to "부추",
            R.id.imgYangbaechu to "양배추",
            R.id.imgPotato to "감자",
            R.id.imgSweetpotato to "고구마",
            R.id.imgGreenonion to "대파",
            R.id.imgScallion to "쪽파",
            R.id.imgSpinach to "시금치",
            R.id.imgBokchoy to "청경채",
            R.id.imgEggplant to "가지",
            R.id.imgCorn to "옥수수",
            R.id.imgBeansprout to "콩나물",
            R.id.imgMungbeansprout to "숙주",
            R.id.imgBeef to "소고기",
            R.id.imgPork to "돼지고기",
            R.id.imgChicken to "닭고기",
            R.id.imgBacon to "베이컨",
            R.id.imgHam to "햄",
            R.id.imgSpam to "스팸",
            R.id.imgEgg to "계란",
            R.id.imgDuck to "오리고기",
            R.id.imgSmokedDuck to "훈제오리",
            R.id.imgChickenBreast to "닭가슴살",
            R.id.imgChickenwing to "닭날개",
            R.id.imgSausage to "소시지",
            R.id.imgQuailegg to "메추리알",
            R.id.imgMackerel to "고등어",
            R.id.imgPollock to "명태",
            R.id.imgSquid to "오징어",
            R.id.imgShrimp to "새우",
            R.id.imgCrab to "게",
            R.id.imgSalmon to "연어",
            R.id.imgTuna to "참치",
            R.id.imgClam to "바지락",
            R.id.imgMussel to "홍합",
            R.id.imgWebfootoctopus to "쭈꾸미",
            R.id.imgOctopus to "낙지",
            R.id.imgScallop to "가리비",
            R.id.imgKetchup to "케찹",
            R.id.imgMayonnaise to "마요네즈",
            R.id.imgSoySauce to "간장",
            R.id.imgGochujang to "고추장",
            R.id.imgDoenjang to "된장",
            R.id.imgSesameOil to "참기름",
            R.id.imgPerillaoil to "들기름",
            R.id.imgButter to "버터",
            R.id.imgChiliPowder to "고춧가루",
            R.id.imgOystersauce to "굴소스",
            R.id.imgCurrypowder to "카레가루",
            R.id.imgMustard to "머스터드",
            R.id.imgTofu to "두부",
            R.id.imgKimchi to "김치",
            R.id.imgMilk to "우유",
            R.id.imgYogurt to "요거트",
            R.id.imgCheese to "치즈",
            R.id.imgSsamjang to "쌈장",
            R.id.imgFrozenDumpling to "냉동만두",
            R.id.imgUdonnoodles to "우동사리",
            R.id.imgBread to "식빵",
            R.id.imgTortilla to "또띠야"
        )

        ingredientClickMap.forEach { (viewId, ingredientName) ->
            val imageView = findViewById<ImageView>(viewId)
            if (imageView != null) {
                imageView.setOnClickListener {
                    showAddDialog(ingredientName)
                }
            } else {
                Log.w("AddIngredientActivity", "재료 아이콘을 찾을 수 없습니다. id=$viewId, name=$ingredientName")
            }
        }

    }

    private fun toggleVisibility(layoutId: Int) {
        val layout = findViewById<View>(layoutId)
        layout?.let {
            it.visibility = if (it.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }

    private fun showAddDialog(ingredientName: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_ingredient, null)
        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_quantity)
        val pickDateButton = dialogView.findViewById<Button>(R.id.btn_pick_date)
        val selectedDateText = dialogView.findViewById<TextView>(R.id.tv_selected_date)

        var selectedDate = ""

        pickDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this,
                { _, year, month, day ->
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                    selectedDateText.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        AlertDialog.Builder(this)
            .setTitle("$ingredientName 추가")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                val quantityText = quantityEditText.text.toString()
                val quantity = quantityText.toIntOrNull()

                if (quantity != null && selectedDate.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val ingredient = db.fridgeDao().getAllIngredients()
                            .find { it.name == ingredientName }

                        if (ingredient != null) {
                            // ✅ 중복 체크: 같은 재료 + 같은 날짜가 이미 있는지 확인
                            val existingItem = db.fridgeDao().getFridgeItemByIngredientAndDate(
                                ingredientId = ingredient.id,
                                expiryDate = selectedDate
                            )

                            if (existingItem != null) {
                                // ✅ 있으면 수량 업데이트
                                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                                db.fridgeDao().updateFridgeItem(updatedItem)

                                runOnUiThread {
                                    Toast.makeText(this@AddIngredientActivity, "${ingredient.name} 수량이 업데이트되었습니다!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // ✅ 없으면 새로 insert
                                val newItem = FridgeItem(
                                    ingredientId = ingredient.id,
                                    quantity = quantity,
                                    expiryDate = selectedDate
                                )
                                db.fridgeDao().insertFridgeItem(newItem)

                                runOnUiThread {
                                    Toast.makeText(this@AddIngredientActivity, "${ingredient.name} 저장 완료!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@AddIngredientActivity, "재료 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "수량과 날짜를 입력하세요!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
