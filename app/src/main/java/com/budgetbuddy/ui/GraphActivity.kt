package com.budgetbuddy.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddy.R
import com.budgetbuddy.data.DBHelper

class GraphActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var tvLegend: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        dbHelper = DBHelper(this)

        tvLegend = findViewById(R.id.tvLegend)

        loadReport()
    }

    private fun loadReport() {

        val cursor = dbHelper.getCategoryTotals()

        val report = StringBuilder()

        while (cursor.moveToNext()) {

            val category = cursor.getString(0)
            val amount = cursor.getDouble(1)

            report.append(
                "$category : R${String.format("%.2f", amount)}\n\n"
            )
        }

        cursor.close()

        tvLegend.text = report.toString()
    }
}