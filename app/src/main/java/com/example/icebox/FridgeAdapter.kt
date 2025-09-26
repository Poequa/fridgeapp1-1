package com.example.icebox

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.icebox.databinding.ItemFridgeBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class FridgeAdapter(
    private var fridgeItems: MutableList<FridgeItemWithName>,
    private val onItemClick: (FridgeItemWithName) -> Unit
) : RecyclerView.Adapter<FridgeAdapter.FridgeViewHolder>() {

    inner class FridgeViewHolder(private val binding: ItemFridgeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(fridgeItemWithName: FridgeItemWithName) {
            val item = fridgeItemWithName.item

            val ingredientName = fridgeItemWithName.name.trim()

            binding.textName.text = ingredientName
            binding.textQuantity.text = "수량: ${item.quantity}"
            binding.textExpire.text = "유통기한: ${item.expiryDate}"

            // ✅ D-day 계산
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val today = LocalDate.now()
            val expiryDate = LocalDate.parse(item.expiryDate, formatter)
            val dday = ChronoUnit.DAYS.between(today, expiryDate).toInt()

            val ddayText = when {
                dday < 0 -> "D+${-dday} (지남)"
                dday == 0 -> "D-DAY"
                else -> "D-$dday"
            }

            binding.textDday.text = ddayText

            // ✅ D-day 색상 강조
            val color = when {
                dday < 0 -> Color.GRAY
                dday <= 1 -> Color.RED
                dday <= 3 -> Color.parseColor("#FFA500") // 주황색
                else -> Color.DKGRAY
            }
            binding.textDday.setTextColor(color)

            // ✅ 이미지 매칭
            val imageRes = INGREDIENT_IMAGE_MAP[ingredientName] ?: R.drawable.ic_default
            binding.imageIngredient.setImageResource(imageRes)

            // ✅ 클릭 이벤트
            binding.root.setOnClickListener {
                onItemClick(fridgeItemWithName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FridgeViewHolder {
        val binding = ItemFridgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FridgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FridgeViewHolder, position: Int) {
        holder.bind(fridgeItems[position])
    }

    override fun getItemCount(): Int = fridgeItems.size

    fun updateItems(newItems: List<FridgeItemWithName>) {
        fridgeItems.clear()
        fridgeItems.addAll(newItems)
        notifyDataSetChanged()
    }
    companion object {
        private val INGREDIENT_IMAGE_MAP = mapOf(
            "토마토" to R.drawable.ic_tomato,
            "당근" to R.drawable.ic_carrot,
            "상추" to R.drawable.ic_lettuce,
            "배추" to R.drawable.ic_cabbage,
            "오이" to R.drawable.ic_cucumber,
            "파프리카" to R.drawable.ic_paprika,
            "양파" to R.drawable.ic_onion,
            "마늘" to R.drawable.ic_garlic,
            "고추" to R.drawable.ic_pepper,
            "브로콜리" to R.drawable.ic_broccoli,
            "표고버섯" to R.drawable.ic_shiitakemushroom,
            "팽이버섯" to R.drawable.ic_enokimushroom,
            "송이버섯" to R.drawable.ic_matsutakemushroom,
            "양송이버섯" to R.drawable.ic_buttonmushroom,
            "새송이버섯" to R.drawable.ic_kingoystermushroom,
            "느타리버섯" to R.drawable.ic_oystermushroom,
            "목이버섯" to R.drawable.ic_woodearmushroom,
            "트러플버섯" to R.drawable.ic_trufflemushroom,
            "애호박" to R.drawable.ic_zucchini,
            "무" to R.drawable.ic_radish,
            "부추" to R.drawable.ic_chive,
            "양배추" to R.drawable.ic_yangbaechu,
            "감자" to R.drawable.ic_potato,
            "고구마" to R.drawable.ic_sweetpotato,
            "대파" to R.drawable.ic_greenonion,
            "쪽파" to R.drawable.ic_scallion,
            "시금치" to R.drawable.ic_spinach,
            "청경채" to R.drawable.ic_bokchoy,
            "가지" to R.drawable.ic_eggplant,
            "옥수수" to R.drawable.ic_corn,
            "콩나물" to R.drawable.ic_beansprout,
            "숙주" to R.drawable.ic_mungbeansprout,
            "소고기" to R.drawable.ic_beef,
            "돼지고기" to R.drawable.ic_pork,
            "닭고기" to R.drawable.ic_chicken,
            "베이컨" to R.drawable.ic_bacon,
            "햄" to R.drawable.ic_ham,
            "스팸" to R.drawable.ic_spam,
            "계란" to R.drawable.ic_egg,
            "오리고기" to R.drawable.ic_duck,
            "훈제오리" to R.drawable.ic_smokedduck,
            "닭가슴살" to R.drawable.ic_chickenbreast,
            "닭날개" to R.drawable.ic_chickenwing,
            "소시지" to R.drawable.ic_sausage,
            "메추리알" to R.drawable.ic_quailegg,
            "고등어" to R.drawable.ic_mackerel,
            "명태" to R.drawable.ic_pollock,
            "오징어" to R.drawable.ic_squid,
            "새우" to R.drawable.ic_shrimp,
            "게" to R.drawable.ic_crab,
            "연어" to R.drawable.ic_salmon,
            "참치" to R.drawable.ic_tuna,
            "바지락" to R.drawable.ic_clam,
            "홍합" to R.drawable.ic_mussel,
            "쭈꾸미" to R.drawable.ic_webfootoctopus,
            "낙지" to R.drawable.ic_octopus,
            "가리비" to R.drawable.ic_scallop,
            "케찹" to R.drawable.ic_ketchup,
            "마요네즈" to R.drawable.ic_mayonnaise,
            "간장" to R.drawable.ic_soy_sauce,
            "고추장" to R.drawable.ic_gochujang,
            "된장" to R.drawable.ic_doenjang,
            "참기름" to R.drawable.ic_sesameoil,
            "들기름" to R.drawable.ic_perillaoil,
            "버터" to R.drawable.ic_butter,
            "고춧가루" to R.drawable.ic_chilipowder,
            "굴소스" to R.drawable.ic_oystersauce,
            "카레가루" to R.drawable.ic_currypowder,
            "머스터드" to R.drawable.ic_mustard,
            "두부" to R.drawable.ic_tofu,
            "김치" to R.drawable.ic_kimchi,
            "우유" to R.drawable.ic_milk,
            "요거트" to R.drawable.ic_yogurt,
            "치즈" to R.drawable.ic_cheese,
            "쌈장" to R.drawable.ic_ssamjang,
            "냉동만두" to R.drawable.ic_frozendumpling,
            "우동사리" to R.drawable.ic_udonnoodles,
            "식빵" to R.drawable.ic_bread,
            "또띠야" to R.drawable.ic_tortilla,
        )
    }
}