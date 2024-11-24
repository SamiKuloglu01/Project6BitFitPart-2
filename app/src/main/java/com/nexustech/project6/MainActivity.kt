package com.nexustech.project6

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), ToolbarTitleListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var currentFragmentTag: String = LogFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        setupBottomNavigation()
        EventBus.getDefault().register(this)

        if (savedInstanceState == null) {
            setDefaultFragment()
        } else {
            currentFragmentTag = savedInstanceState.getString("currentFragmentTag", LogFragment::class.java.simpleName)
            supportFragmentManager.findFragmentByTag(currentFragmentTag)?.let {
                replaceFragment(it)
            }
        }
    }

    private fun initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFoodRecordAdded(event: FoodRecordAddedEvent) {
        Log.d("MainActivity", "Food added: ${event.foodName}, Calories: ${event.foodCalories}")
        replaceFragment(DashboardFragment(), DashboardFragment::class.java.simpleName)
        bottomNavigationView.selectedItemId = R.id.action_dashboard
    }

    private fun setDefaultFragment() {
        replaceFragment(LogFragment(), LogFragment::class.java.simpleName)
        bottomNavigationView.selectedItemId = R.id.action_log
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.action_log -> LogFragment()
                R.id.action_dashboard -> DashboardFragment()
                else -> null
            }
            selectedFragment?.let {
                replaceFragment(it, it::class.java.simpleName)
                return@setOnItemSelectedListener true
            }
            false
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String? = null) {
        currentFragmentTag = tag ?: fragment::class.java.simpleName
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)

        if (supportFragmentManager.isStateSaved) {
            transaction.commitAllowingStateLoss()
        } else {
            transaction.commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentFragmentTag", currentFragmentTag)
    }
    override fun updateToolbar(iconResId: Int, title: String) {
        val toolbarIcon = findViewById<ImageView>(R.id.toolbar_icon)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarIcon.setImageResource(iconResId)
        toolbarTitle.text = title
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
