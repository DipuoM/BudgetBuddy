# BudgetBuddy - Personal Budget Tracker

## Overview

BudgetBuddy is an Android mobile application developed in Kotlin using Android Studio. The application helps users manage their personal finances by tracking expenses, organizing spending into categories, and viewing spending analytics through graphical reports.

This project was developed as part of a Mobile Application Development Portfolio of Evidence (POE).

---

## Features

### User Authentication

* User login functionality
* User registration functionality
* SQLite database storage for user accounts

### Expense Management

* Add new expenses
* Record expense title, amount, date, category, and receipt image
* Store expense information in SQLite database

### Category Management

* Add custom categories
* Edit existing categories
* Delete categories
* View all categories

### Expense Tracking

* View all expenses
* Filter expenses by date range
* Display total expenses
* View expense details including images

### Analytics and Reporting

* Category spending reports
* Pie chart visualization of expenses
* Percentage spending breakdown by category
* Total spending summaries

### Image Support

* Capture or attach receipt images
* Store image paths in SQLite database
* Display images in expense records

---

## Technologies Used

* Kotlin
* Android Studio
* SQLite Database
* RecyclerView
* Custom Pie Chart View
* Android SDK
* Material Design Components

---

## Database Tables

### Users

| Field    | Type    |
| -------- | ------- |
| id       | Integer |
| username | Text    |
| password | Text    |

### Categories

| Field | Type    |
| ----- | ------- |
| id    | Integer |
| name  | Text    |

### Expenses

| Field       | Type    |
| ----------- | ------- |
| id          | Integer |
| title       | Text    |
| amount      | Real    |
| date        | Text    |
| category_id | Integer |
| image_path  | Text    |

---

## Installation

1. Clone the repository:
   git clone https://github.com/DipuoM/BudgetBuddy.git

2. Open the project in Android Studio.

3. Allow Gradle to sync.

4. Run the application on an emulator or Android device.

---

## Project Structure

* data/ - Database operations
* adapter/ - RecyclerView adapters
* ui/ - Activities and screens
* custom/ - Custom Pie Chart implementation
* model/ - Data models
* res/ - Layouts and resources

---

## Future Improvements

* Monthly budgets
* Expense editing screen
* Export reports to PDF
* Cloud backup support
* Dark mode support
* User profile management

---

## Author

BudgetBuddy Mobile Application

Developed for academic purposes as part of a Mobile Application Development Portfolio of Evidence (POE).

---

youtube link 
https://youtu.be/oGH8lovzxLc

