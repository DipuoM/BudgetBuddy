package com.budgetbuddy.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddy.R
import com.budgetbuddy.data.DBHelper
import com.budgetbuddy.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var sessionManager: SessionManager

    private lateinit var tvGreeting: TextView
    private lateinit var tvBudgetStatus: TextView
    private lateinit var tvCurrentTotal: TextView
    private lateinit var progressBudget: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        sessionManager = SessionManager(this)

        val userId = sessionManager.getUserId()

        if (userId == -1) {
            logout()
            return
        }

        tvGreeting = findViewById(R.id.tvGreeting)
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus)
        tvCurrentTotal = findViewById(R.id.tvCurrentTotal)
        progressBudget = findViewById(R.id.progressBudget)

        val btnAddExpense = findViewById<View>(R.id.btnAddExpense)
        val btnViewExpenses = findViewById<View>(R.id.btnViewExpenses)
        val btnManageCategories = findViewById<View>(R.id.btnManageCategories)
        val btnCategoryReport = findViewById<View>(R.id.btnCategoryReport)
        val btnSetBudget = findViewById<View>(R.id.btnSetBudget)
        val btnLogout = findViewById<View>(R.id.btnLogout)

        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        btnViewExpenses.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }

        btnManageCategories.setOnClickListener {
            startActivity(Intent(this, ManageCategoriesActivity::class.java))
        }

        btnCategoryReport.setOnClickListener {
            startActivity(
                Intent(this, GraphActivity::class.java)
            )
        }
        btnSetBudget.setOnClickListener {
            showSetBudgetDialog()
        }

        btnLogout.setOnClickListener {
            logout()
        }

        updateDashboard()
    }

    override fun onResume() {
        super.onResume()
        updateDashboard()
    }

    private fun updateDashboard() {

        val userId = sessionManager.getUserId()

        val currentMonth =
            SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        val totalSpent =
            dbHelper.getTotalExpensesForCurrentMonth(userId, currentMonth)

        tvCurrentTotal.text = "Current Spending: R%.2f".format(totalSpent)

        val minGoal = sessionManager.getMonthlyMinGoal(userId)
        val maxGoal = sessionManager.getMonthlyMaxGoal(userId)

        if (maxGoal > 0) {

            val percentage =
                ((totalSpent / maxGoal) * 100).toInt().coerceIn(0, 100)

            progressBudget.progress = percentage

            when {
                totalSpent < minGoal -> {
                    tvBudgetStatus.text =
                        "⚠ Below Minimum Goal (R%.2f)".format(minGoal)
                }

                totalSpent > maxGoal -> {
                    tvBudgetStatus.text =
                        "🚨 Over Budget! Maximum Goal: R%.2f".format(maxGoal)
                }

                else -> {
                    tvBudgetStatus.text =
                        "✅ Within Budget Range"
                }
            }

        } else {
            progressBudget.progress = 0
            tvBudgetStatus.text = "Set your monthly budget goals"
        }

        tvGreeting.text = "Welcome Back!"
    }

    private fun showSetBudgetDialog() {

        val userId = sessionManager.getUserId()

        val dialogView =
            layoutInflater.inflate(R.layout.dialog_budget, null)

        val etMinGoal =
            dialogView.findViewById<EditText>(R.id.etMinGoal)

        val etMaxGoal =
            dialogView.findViewById<EditText>(R.id.etMaxGoal)

        etMinGoal.setText(
            sessionManager.getMonthlyMinGoal(userId).toString()
        )

        etMaxGoal.setText(
            sessionManager.getMonthlyMaxGoal(userId).toString()
        )

        AlertDialog.Builder(this)
            .setTitle("Monthly Budget Goals")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->

                val minGoal =
                    etMinGoal.text.toString().toDoubleOrNull() ?: 0.0

                val maxGoal =
                    etMaxGoal.text.toString().toDoubleOrNull() ?: 0.0

                if (maxGoal >= minGoal) {

                    sessionManager.setMonthlyMinGoal(userId, minGoal)
                    sessionManager.setMonthlyMaxGoal(userId, maxGoal)

                    updateDashboard()

                    Toast.makeText(
                        this,
                        "Budget Goals Saved",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Maximum goal must be greater than minimum goal",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {

        sessionManager.clearSession()

        startActivity(
            Intent(this, LoginActivity::class.java)
        )

        finish()
    }
}