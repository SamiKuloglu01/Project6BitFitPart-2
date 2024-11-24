package com.nexustech.project6


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FoodDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "FoodTracker.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "FoodEntries"
        const val COLUMN_ID = "id"
        const val COLUMN_FOOD_NAME = "food_name"
        const val COLUMN_CALORIES = "calories"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FOOD_NAME TEXT,
                $COLUMN_CALORIES INTEGER
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert Data into the Table
    fun insertFood(foodName: String, calories: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FOOD_NAME, foodName)
            put(COLUMN_CALORIES, calories)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L // Returns true if insertion is successful
    }

    // Retrieve All Data
    fun getAllFoodEntries(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    // Delete All Data
    fun clearAllData() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        db.close()
    }
}
