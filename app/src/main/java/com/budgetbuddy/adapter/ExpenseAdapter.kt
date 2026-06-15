package com.budgetbuddy.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.budgetbuddy.R
import java.io.File

class ExpenseAdapter(
    private val context: Context,
    private var expenseList: List<ExpenseItem>,
    private val onDeleteClick: (ExpenseItem) -> Unit,
    private val onEditClick: (ExpenseItem) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    data class ExpenseItem(
        val id: Int,
        val title: String,
        val amount: Double,
        val date: String,
        val category: String,
        val imagePath: String?
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_expense, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = expenseList[position]

        holder.tvTitle.text = item.title
        holder.tvAmount.text = "R${item.amount}"
        holder.tvDate.text = item.date
        holder.tvCategory.text = item.category

        if (!item.imagePath.isNullOrEmpty()) {

            val imgFile = File(item.imagePath)

            if (imgFile.exists()) {

                val bmp =
                    BitmapFactory.decodeFile(imgFile.absolutePath)

                holder.ivImage.setImageBitmap(bmp)
                holder.ivImage.visibility = View.VISIBLE

            } else {

                holder.ivImage.visibility = View.GONE
            }

        } else {

            holder.ivImage.visibility = View.GONE
        }

        // Tap = Edit
        holder.itemView.setOnClickListener {
            onEditClick(item)
        }

        // Long Press = Delete
        holder.itemView.setOnLongClickListener {
            onDeleteClick(item)
            true
        }
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    fun updateList(newList: List<ExpenseItem>) {
        expenseList = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView =
            itemView.findViewById(R.id.tvExpenseTitle)

        val tvAmount: TextView =
            itemView.findViewById(R.id.tvExpenseAmount)

        val tvDate: TextView =
            itemView.findViewById(R.id.tvExpenseDate)

        val tvCategory: TextView =
            itemView.findViewById(R.id.tvExpenseCategory)

        val ivImage: ImageView =
            itemView.findViewById(R.id.ivExpenseImage)
    }
}