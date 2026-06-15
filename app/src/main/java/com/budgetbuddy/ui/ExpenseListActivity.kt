package com.budgetbuddy.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.budgetbuddy.R
import com.budgetbuddy.adapter.ExpenseAdapter
import com.budgetbuddy.data.DBHelper
import java.util.Calendar

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private lateinit var btnFilter: Button
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView

    private var startDate = ""
    private var endDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        dbHelper = DBHelper(this)

        recyclerView = findViewById(R.id.rvExpenses)
        btnFilter = findViewById(R.id.btnFilter)
        tvStartDate = findViewById(R.id.tvStartDate)
        tvEndDate = findViewById(R.id.tvEndDate)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ExpenseAdapter(
            this,
            mutableListOf(),

            onDeleteClick = { expense ->
                showDeleteDialog(expense)
            },

            onEditClick = { expense ->
                showEditDialog(expense)
            }
        )

        recyclerView.adapter = adapter

        tvStartDate.setOnClickListener {
            showDatePicker(true)
        }

        tvEndDate.setOnClickListener {
            showDatePicker(false)
        }

        btnFilter.setOnClickListener {
            loadExpenses()
        }

        loadExpenses()
    }

    private fun showDatePicker(isStart: Boolean) {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, day: Int ->

                val date = String.format(
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

    private fun loadExpenses() {

        val cursor: Cursor =
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                dbHelper.getExpensesBetweenDates(
                    startDate,
                    endDate
                )
            } else {
                dbHelper.getAllExpenses()
            }

        val expenses =
            mutableListOf<ExpenseAdapter.ExpenseItem>()

        var totalAmount = 0.0

        while (cursor.moveToNext()) {

            val id = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                    DBHelper.COL_EXPENSE_ID
                )
            )

            val title = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DBHelper.COL_TITLE
                )
            )

            val amount = cursor.getDouble(
                cursor.getColumnIndexOrThrow(
                    DBHelper.COL_AMOUNT
                )
            )

            val date = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DBHelper.COL_DATE
                )
            )

            val category = cursor.getString(
                cursor.getColumnIndexOrThrow("name")
            )

            val imagePath = cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DBHelper.COL_IMAGE_PATH
                )
            )

            totalAmount += amount

            expenses.add(
                ExpenseAdapter.ExpenseItem(
                    id,
                    title,
                    amount,
                    date,
                    category,
                    imagePath
                )
            )
        }

        cursor.close()

        adapter.updateList(expenses)

        supportActionBar?.subtitle =
            "Expenses: ${expenses.size} | Total: R${String.format("%.2f", totalAmount)}"
    }

    private fun showDeleteDialog(
        expense: ExpenseAdapter.ExpenseItem
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Expense")
            .setMessage("Delete '${expense.title}'?")
            .setPositiveButton("Delete") { _, _ ->

                val success =
                    dbHelper.deleteExpense(expense.id)

                if (success) {

                    Toast.makeText(
                        this,
                        "Expense Deleted",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadExpenses()

                } else {

                    Toast.makeText(
                        this,
                        "Delete Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(
        expense: ExpenseAdapter.ExpenseItem
    ) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val etTitle = EditText(this)
        etTitle.setText(expense.title)

        val etAmount = EditText(this)
        etAmount.setText(expense.amount.toString())

        layout.addView(etTitle)
        layout.addView(etAmount)

        AlertDialog.Builder(this)
            .setTitle("Edit Expense")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->

                val title =
                    etTitle.text.toString().trim()

                val amount =
                    etAmount.text.toString()
                        .toDoubleOrNull() ?: 0.0

                val success =
                    dbHelper.updateExpense(
                        expense.id,
                        title,
                        amount
                    )

                if (success) {

                    Toast.makeText(
                        this,
                        "Expense Updated",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadExpenses()

                } else {

                    Toast.makeText(
                        this,
                        "Update Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}