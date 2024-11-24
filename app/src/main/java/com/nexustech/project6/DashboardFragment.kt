package com.nexustech.project6

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class DashboardFragment : Fragment() {

    private lateinit var dbHelper: FoodDatabaseHelper
    private lateinit var avgCaloriesView: TextView
    private lateinit var minCaloriesView: TextView
    private lateinit var maxCaloriesView: TextView
    private lateinit var clearDataButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize database helper
        dbHelper = FoodDatabaseHelper(requireContext())

        // Initialize views
        avgCaloriesView = view.findViewById(R.id.value_average_calories)
        minCaloriesView = view.findViewById(R.id.value_minimum_calories)
        maxCaloriesView = view.findViewById(R.id.value_maximum_calories)
        clearDataButton = view.findViewById(R.id.btn_clear_data)
        (activity as? MainActivity)?.updateToolbar(R.drawable.ic_dashboard, "Dashboard")

        // Load data and set up button listener
        loadDataFromDatabase()
        clearDataButton.setOnClickListener {
            clearData()
        }
        val btnAddNewFood: Button = view.findViewById(R.id.btnAddNewFood)
        btnAddNewFood.setOnClickListener {
            navigateToAddRecordActivity()
        }
        return view
    }

    private fun navigateToAddRecordActivity() {
        val intent = Intent(activity, AddRecordActivity::class.java)
        startActivity(intent)
    }

    private fun loadDataFromDatabase() {
        val cursor: Cursor = dbHelper.getAllFoodEntries()
        if (cursor.count > 0) {
            var totalCalories = 0
            var minCalories = Int.MAX_VALUE
            var maxCalories = Int.MIN_VALUE

            while (cursor.moveToNext()) {
                val calories = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_CALORIES))
                totalCalories += calories
                if (calories < minCalories) minCalories = calories
                if (calories > maxCalories) maxCalories = calories
            }

            cursor.close()

            val avgCalories = totalCalories / cursor.count

            // Update UI
            avgCaloriesView.text = avgCalories.toString()
            minCaloriesView.text = minCalories.toString()
            maxCaloriesView.text = maxCalories.toString()
        } else {
            // No data available
            avgCaloriesView.text = "-"
            minCaloriesView.text = "-"
            maxCaloriesView.text = "-"
        }
    }

    private fun clearData() {
        dbHelper.clearAllData()
        Toast.makeText(requireContext(), "All data cleared.", Toast.LENGTH_SHORT).show()
        loadDataFromDatabase()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}
