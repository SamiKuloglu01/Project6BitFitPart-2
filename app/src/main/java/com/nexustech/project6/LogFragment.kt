package com.nexustech.project6

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LogFragment : Fragment() {

    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter
    private val foodList = mutableListOf<FoodItem>()
    private lateinit var foodDatabaseHelper: FoodDatabaseHelper
    private lateinit var noFoodItemsMessage: TextView // TextView for "No Food items" message

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_log, container, false)

        initializeUI(view)
        (activity as? MainActivity)?.updateToolbar(R.drawable.ic_log, "Log")
        initializeDatabase()
        loadFoodData()
        setupRecyclerView()

        return view
    }

    /**
     * Initialize the UI components like RecyclerView, Button, and TextView.
     */
    private fun initializeUI(view: View) {
        foodRecyclerView = view.findViewById(R.id.recyclerViewFood)
        foodRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        noFoodItemsMessage = view.findViewById(R.id.noFoodItemsMessage) // Initialize TextView
        val btnAddNewFood: Button = view.findViewById(R.id.btnAddNewFood)
        btnAddNewFood.setOnClickListener {
            navigateToAddRecordActivity()
        }
    }

    /**
     * Initialize the FoodDatabaseHelper for database operations.
     */
    private fun initializeDatabase() {
        foodDatabaseHelper = FoodDatabaseHelper(requireContext())
    }

    /**
     * Fetch food data from the database and update the food list.
     */
    private fun loadFoodData() {
        foodList.clear()
        val cursor = foodDatabaseHelper.getAllFoodEntries()
        parseFoodData(cursor)
    }

    /**
     * Parse data from the database cursor and populate the food list.
     */
    private fun parseFoodData(cursor: Cursor) {
        if (cursor.moveToFirst()) {
            do {
                val foodName = cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_FOOD_NAME))
                val calories = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_CALORIES))
                foodList.add(FoodItem(foodName, calories))
            } while (cursor.moveToNext())
        }
        cursor.close()

        // After loading the data, update the visibility of RecyclerView and message
        updateUI()
    }

    /**
     * Set up the RecyclerView and its adapter.
     */
    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(foodList)
        foodRecyclerView.adapter = foodAdapter
    }

    /**
     * Update the UI based on whether there are food items or not.
     */
    private fun updateUI() {
        if (foodList.isEmpty()) {
            foodRecyclerView.visibility = View.INVISIBLE // Hide RecyclerView
            noFoodItemsMessage.visibility = View.VISIBLE // Show "No Food items" message
        } else {
            foodRecyclerView.visibility = View.VISIBLE // Show RecyclerView
            noFoodItemsMessage.visibility = View.GONE // Hide "No Food items" message
        }
    }

    /**
     * Navigate to AddRecordActivity when the add button is clicked.
     */
    private fun navigateToAddRecordActivity() {
        val intent = Intent(activity, AddRecordActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        foodDatabaseHelper.close()
    }
}
