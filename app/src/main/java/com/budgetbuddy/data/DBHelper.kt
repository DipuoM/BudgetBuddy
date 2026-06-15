package com.budgetbuddy.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "budgetbuddy.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val TABLE_CATEGORIES = "categories"
        const val TABLE_EXPENSES = "expenses"

        const val COL_USER_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"

        const val COL_CATEGORY_ID = "id"
        const val COL_CATEGORY_NAME = "name"

        const val COL_EXPENSE_ID = "id"
        const val COL_TITLE = "title"
        const val COL_AMOUNT = "amount"
        const val COL_DATE = "date"
        const val COL_CATEGORY_ID_FK = "category_id"
        const val COL_IMAGE_PATH = "image_path"
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE NOT NULL,
                $COL_PASSWORD TEXT NOT NULL
            )
        """
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_CATEGORIES (
                $COL_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CATEGORY_NAME TEXT UNIQUE NOT NULL
            )
        """
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_EXPENSES (
                $COL_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_AMOUNT REAL NOT NULL,
                $COL_DATE TEXT NOT NULL,
                $COL_CATEGORY_ID_FK INTEGER NOT NULL,
                $COL_IMAGE_PATH TEXT,
                FOREIGN KEY ($COL_CATEGORY_ID_FK)
                REFERENCES $TABLE_CATEGORIES($COL_CATEGORY_ID)
            )
        """
        )

        insertDefaultCategories(db)
        insertDemoUser(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun insertDefaultCategories(db: SQLiteDatabase) {
        val categories = listOf(
            "Food",
            "Transport",
            "Shopping",
            "Entertainment",
            "Bills",
            "Health"
        )

        categories.forEach {
            val cv = ContentValues()
            cv.put(COL_CATEGORY_NAME, it)
            db.insert(TABLE_CATEGORIES, null, cv)
        }
    }


    private fun insertDemoUser(db: SQLiteDatabase) {
        val cv = ContentValues()
        cv.put(COL_USERNAME, "buddy")
        cv.put(COL_PASSWORD, "buddy123")
        db.insert(TABLE_USERS, null, cv)
    }

    fun registerUser(username: String, password: String): Boolean {
        val cv = ContentValues()
        cv.put(COL_USERNAME, username)
        cv.put(COL_PASSWORD, password)

        return writableDatabase.insert(
            TABLE_USERS,
            null,
            cv
        ) != -1L
    }

    fun loginUser(username: String, password: String): Int {

        val cursor = readableDatabase.query(
            TABLE_USERS,
            arrayOf(COL_USER_ID),
            "$COL_USERNAME=? AND $COL_PASSWORD=?",
            arrayOf(username, password),
            null,
            null,
            null
        )

        val id =
            if (cursor.moveToFirst()) cursor.getInt(0)
            else -1

        cursor.close()
        return id
    }

    fun getAllCategories(): List<Pair<Int, String>> {

        val list = mutableListOf<Pair<Int, String>>()

        val cursor = readableDatabase.query(
            TABLE_CATEGORIES,
            arrayOf(COL_CATEGORY_ID, COL_CATEGORY_NAME),
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            list.add(
                Pair(
                    cursor.getInt(0),
                    cursor.getString(1)
                )
            )
        }

        cursor.close()
        return list
    }

    fun updateCategory(
        oldName: String,
        newName: String
    ): Boolean {

        val cv = ContentValues()
        cv.put(COL_CATEGORY_NAME, newName)

        val result = writableDatabase.update(
            TABLE_CATEGORIES,
            cv,
            "$COL_CATEGORY_NAME=?",
            arrayOf(oldName)
        )

        return result > 0
    }

    fun deleteCategory(categoryName: String): Boolean {

        val result = writableDatabase.delete(
            TABLE_CATEGORIES,
            "$COL_CATEGORY_NAME=?",
            arrayOf(categoryName)
        )

        return result > 0
    }

    fun addCategory(name: String): Boolean {

        val cv = ContentValues()
        cv.put(COL_CATEGORY_NAME, name)

        return writableDatabase.insert(
            TABLE_CATEGORIES,
            null,
            cv
        ) != -1L
    }



    fun addExpense(
        title: String,
        amount: Double,
        date: String,
        categoryId: Int,
        imagePath: String?
    ): Boolean {

        val cv = ContentValues()
        cv.put(COL_TITLE, title)
        cv.put(COL_AMOUNT, amount)
        cv.put(COL_DATE, date)
        cv.put(COL_CATEGORY_ID_FK, categoryId)
        cv.put(COL_IMAGE_PATH, imagePath)

        return writableDatabase.insert(
            TABLE_EXPENSES,
            null,
            cv
        ) != -1L
    }

    fun deleteExpense(expenseId: Int): Boolean {

        val result = writableDatabase.delete(
            TABLE_EXPENSES,
            "$COL_EXPENSE_ID=?",
            arrayOf(expenseId.toString())
        )

        return result > 0
    }
    fun updateExpense(
        id: Int,
        title: String,
        amount: Double
    ): Boolean {

        val cv = ContentValues()

        cv.put(COL_TITLE, title)
        cv.put(COL_AMOUNT, amount)

        val result = writableDatabase.update(
            TABLE_EXPENSES,
            cv,
            "$COL_EXPENSE_ID=?",
            arrayOf(id.toString())
        )

        return result > 0
    }


    fun getAllExpenses(): Cursor {
        return readableDatabase.rawQuery(
            """
            SELECT e.*, c.$COL_CATEGORY_NAME
            FROM $TABLE_EXPENSES e
            JOIN $TABLE_CATEGORIES c
            ON e.$COL_CATEGORY_ID_FK = c.$COL_CATEGORY_ID
            ORDER BY e.$COL_DATE DESC
            """,
            null
        )
    }

    fun getExpensesBetweenDates(
        startDate: String,
        endDate: String
    ): Cursor {

        return readableDatabase.rawQuery(
            """
            SELECT e.*, c.$COL_CATEGORY_NAME
            FROM $TABLE_EXPENSES e
            JOIN $TABLE_CATEGORIES c
            ON e.$COL_CATEGORY_ID_FK = c.$COL_CATEGORY_ID
            WHERE e.$COL_DATE BETWEEN ? AND ?
            ORDER BY e.$COL_DATE DESC
            """,
            arrayOf(startDate, endDate)
        )
    }

    fun getCategoryTotals(
        startDate: String? = null,
        endDate: String? = null
    ): Cursor {

        val query =
            if (startDate != null && endDate != null) {
                """
                SELECT c.$COL_CATEGORY_NAME,
                SUM(e.$COL_AMOUNT) AS total
                FROM $TABLE_EXPENSES e
                JOIN $TABLE_CATEGORIES c
                ON e.$COL_CATEGORY_ID_FK = c.$COL_CATEGORY_ID
                WHERE e.$COL_DATE BETWEEN ? AND ?
                GROUP BY c.$COL_CATEGORY_NAME
                ORDER BY total DESC
                """
            } else {
                """
                SELECT c.$COL_CATEGORY_NAME,
                SUM(e.$COL_AMOUNT) AS total
                FROM $TABLE_EXPENSES e
                JOIN $TABLE_CATEGORIES c
                ON e.$COL_CATEGORY_ID_FK = c.$COL_CATEGORY_ID
                GROUP BY c.$COL_CATEGORY_NAME
                ORDER BY total DESC
                """
            }

        return if (startDate != null && endDate != null) {
            readableDatabase.rawQuery(
                query,
                arrayOf(startDate, endDate)
            )
        } else {
            readableDatabase.rawQuery(query, null)
        }
    }

    fun getTotalExpensesForCurrentMonth(
        userId: Int,
        yearMonth: String
    ): Double {

        val cursor = readableDatabase.rawQuery(
            """
            SELECT SUM($COL_AMOUNT)
            FROM $TABLE_EXPENSES
            WHERE $COL_DATE LIKE ?
            """,
            arrayOf("$yearMonth%")
        )

        var total = 0.0

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }

        cursor.close()
        return total
    }
}
