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

            binding.textName.text = fridgeItemWithName.name
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
            val imageRes = when (fridgeItemWithName.name.trim()) {
                "당근" -> R.drawable.ic_carrot
                "닭고기" -> R.drawable.ic_chicken
                "소고기" -> R.drawable.ic_beef
                "돼지고기" -> R.drawable.ic_pork
                "케찹" -> R.drawable.ic_ketchup
                "상추" -> R.drawable.ic_lettuce
                "간장" -> R.drawable.ic_soy_sauce
                "오이" -> R.drawable.ic_cucumber
                else -> R.drawable.ic_default
            }
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
}
