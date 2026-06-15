package com.budgetbuddy.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddy.R
import com.budgetbuddy.data.DBHelper

class ManageCategoriesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private val categories = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_categories)

        dbHelper = DBHelper(this)

        listView = findViewById(R.id.lvCategories)

        val btnAddCategory =
            findViewById<Button>(R.id.btnAddCategory)

        loadCategories()

        btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        listView.setOnItemClickListener { _, _, position, _ ->

            showEditDialog(
                categories[position]
            )
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->

            showDeleteDialog(
                categories[position]
            )

            true
        }
    }

    private fun loadCategories() {

        categories.clear()

        categories.addAll(
            dbHelper.getAllCategories()
                .map { it.second }
        )

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            categories
        )

        listView.adapter = adapter
    }

    private fun showAddCategoryDialog() {

        val input = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("Add Category")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->

                val name =
                    input.text.toString().trim()

                if (name.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Category name required",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val success =
                    dbHelper.addCategory(name)

                if (success) {

                    Toast.makeText(
                        this,
                        "Category Added",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadCategories()

                } else {

                    Toast.makeText(
                        this,
                        "Category already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(
        oldCategory: String
    ) {

        val input = EditText(this)
        input.setText(oldCategory)

        AlertDialog.Builder(this)
            .setTitle("Edit Category")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->

                val newCategory =
                    input.text.toString().trim()

                if (newCategory.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Category name required",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val success =
                    dbHelper.updateCategory(
                        oldCategory,
                        newCategory
                    )

                if (success) {

                    Toast.makeText(
                        this,
                        "Category Updated",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadCategories()

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

    private fun showDeleteDialog(
        category: String
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("Delete '$category'?")
            .setPositiveButton("Delete") { _, _ ->

                val success =
                    dbHelper.deleteCategory(category)

                if (success) {

                    Toast.makeText(
                        this,
                        "Category Deleted",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadCategories()

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
}