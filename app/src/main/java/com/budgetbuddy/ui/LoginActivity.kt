package com.budgetbuddy.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budgetbuddy.R
import com.budgetbuddy.data.DBHelper
import com.budgetbuddy.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)
        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnLogin.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                etUsername.error = "Username required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password required"
                return@setOnClickListener
            }

            val userId = dbHelper.loginUser(username, password)

            if (userId != -1) {

                sessionManager.saveLoginSession(userId)

                Toast.makeText(
                    this,
                    "Login Successful",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {

                Toast.makeText(
                    this,
                    "Invalid username or password",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        btnRegister.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                etUsername.error = "Username required"
                return@setOnClickListener
            }

            if (password.length < 4) {
                etPassword.error = "Minimum 4 characters"
                return@setOnClickListener
            }

            val registered = dbHelper.registerUser(username, password)

            if (registered) {

                Toast.makeText(
                    this,
                    "Registration Successful. Please Login.",
                    Toast.LENGTH_LONG
                ).show()

                etUsername.text.clear()
                etPassword.text.clear()

            } else {

                Toast.makeText(
                    this,
                    "Username already exists",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
