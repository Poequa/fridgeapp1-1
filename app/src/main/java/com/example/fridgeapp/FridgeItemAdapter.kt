package com.example.fridgeapp.ui

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.fridgeapp.R
import com.example.fridgeapp.data.FridgeItemWithName
import java.text.SimpleDateFormat
import java.util.*

class FridgeItemAdapter(
    private val context: Context,
    private val items: MutableList<FridgeItemWithName>,
    private val onDelete: (FridgeItemWithName, Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): FridgeItemWithName = items[position]
    override fun getItemId(position: Int): Long = items[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_fridge, parent, false)
        val item = getItem(position)

        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        val expiryText = item.expiryDate?.let { sdf.format(Date(it)) } ?: "없음"

        view.findViewById<TextView>(R.id.itemText).text =
            "재료: ${item.name}, 수량: ${item.quantity}, 등록일: ${sdf.format(Date(item.addDate))}, 유통기한: $expiryText"

        view.findViewById<Button>(R.id.deleteButton).setOnClickListener {
            showDeleteDialog(item)
        }

        return view
    }

    private fun showDeleteDialog(item: FridgeItemWithName) {
        val input = EditText(context).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            hint = "삭제할 수량 입력"
        }

        AlertDialog.Builder(context)
            .setTitle("${item.name} 삭제")
            .setMessage("삭제할 수량을 입력하세요:")
            .setView(input)
            .setPositiveButton("삭제") { _, _ ->
                val quantity = input.text.toString().toIntOrNull() ?: 0
                if (quantity > 0) onDelete(item, quantity)
                else Toast.makeText(context, "올바른 수량을 입력하세요", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
