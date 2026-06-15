package com.budgetbuddy.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.budgetbuddy.R
import com.budgetbuddy.data.DBHelper
import java.util.*

class CategoryReportActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnFilter: Button
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView

    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_report)

        dbHelper = DBHelper(this)

        recyclerView = findViewById(R.id.rvCategoryTotals)
        btnFilter = findViewById(R.id.btnFilterReport)
        tvStartDate = findViewById(R.id.tvReportStart)
        tvEndDate = findViewById(R.id.tvReportEnd)

        recyclerView.layoutManager = LinearLayoutManager(this)

        tvStartDate.setOnClickListener {
            showDatePicker(true)
        }

        tvEndDate.setOnClickListener {
            showDatePicker(false)
        }

        btnFilter.setOnClickListener {
            loadReport()
        }

        loadReport()
    }

    private fun showDatePicker(isStart: Boolean) {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, day: Int ->

                val date =
                    String.format(
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        day
                    )

                if (isStart) {
                    startDate = date
                    tvStartDate.text = date
                } else {
                    endDate = date
                    tvEndDate.text = date
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadReport() {

        val cursor =
            if (startDate != null && endDate != null) {
                dbHelper.getCategoryTotals(startDate, endDate)
            } else {
                dbHelper.getCategoryTotals()
            }

        val totals = mutableListOf<CategoryTotal>()

        var grandTotal = 0.0
        var highestCategory = ""
        var highestAmount = 0.0

        while (cursor.moveToNext()) {

            val category = cursor.getString(0)
            val total = cursor.getDouble(1)

            grandTotal += total

            if (total > highestAmount) {
                highestAmount = total
                highestCategory = category
            }

            totals.add(
                CategoryTotal(
                    category,
                    total
                )
            )
        }

        cursor.close()

        recyclerView.adapter = CategoryTotalsAdapter(totals)

        supportActionBar?.subtitle =
            "Total Spending: R${String.format("%.2f", grandTotal)}"

        if (highestCategory.isNotEmpty()) {

            title =
                "Top Category: $highestCategory (R${String.format("%.2f", highestAmount)})"
        }
    }

    data class CategoryTotal(
        val category: String,
        val total: Double
    )

    inner class CategoryTotalsAdapter(
        private val list: List<CategoryTotal>
    ) : RecyclerView.Adapter<CategoryTotalsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {

            val view = layoutInflater.inflate(
                android.R.layout.simple_list_item_2,
                parent,
                false
            )

            return ViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {

            val item = list[position]

            holder.text1.text = item.category
            holder.text2.text = "R${String.format("%.2f", item.total)}"
        }

        override fun getItemCount(): Int = list.size

        inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

            val text1: TextView =
                itemView.findViewById(android.R.id.text1)

            val text2: TextView =
                itemView.findViewById(android.R.id.text2)
        }
    }
}
