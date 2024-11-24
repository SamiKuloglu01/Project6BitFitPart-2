package com.nexustech.project6

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.greenrobot.eventbus.EventBus

class AddRecordActivity : AppCompatActivity() {

    private lateinit var dbHelper: FoodDatabaseHelper
    private lateinit var editFood: EditText
    private lateinit var editCalories: EditText
    private lateinit var btnRecordFood: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)

        // Initialize components
        initializeViews()
        initializeDatabaseHelper()
        setButtonClickListener()

    }

    private fun initializeViews() {
        editFood = findViewById(R.id.edit_food)
        editCalories = findViewById(R.id.edit_calories)
        btnRecordFood = findViewById(R.id.btn_record_food)
    }


    private fun initializeDatabaseHelper() {
        dbHelper = FoodDatabaseHelper(this)
    }


    private fun setButtonClickListener() {
        btnRecordFood.setOnClickListener {
            handleRecordButtonClick()
        }
    }

    private fun handleRecordButtonClick() {
        val foodName = editFood.text.toString().trim()
        val calorieInput = editCalories.text.toString().trim()

        if (validateInputs(foodName, calorieInput)) {
            insertFoodRecord(foodName, calorieInput)
        }
    }

    private fun validateInputs(foodName: String, calorieInput: String): Boolean {
        return if (foodName.isEmpty() || calorieInput.isEmpty()) {
            showToast("Please enter both food name and calories")
            false
        } else {
            true
        }
    }

    private fun insertFoodRecord(foodName: String, calorieInput: String) {
        try {
            val calories = calorieInput.toInt()

            val isInserted = dbHelper.insertFood(foodName, calories)
            if (isInserted) {
                showToast("Food entry recorded successfully!")
                clearInputFields()
                val event = FoodRecordAddedEvent(foodName, calories)

                EventBus.getDefault().post(event)
                finish()
            } else {
                showToast("Failed to record food entry.")
            }
        } catch (e: NumberFormatException) {
            showToast("Please enter a valid number for calories.")
        }
    }

    private fun clearInputFields() {
        editFood.text.clear()
        editCalories.text.clear()
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }

}
