package com.example.icebox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.example.icebox.R
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.nav_add -> startActivity(Intent(this, AddIngredientActivity::class.java))
                R.id.nav_list -> startActivity(Intent(this, FridgeListActivity::class.java))
                R.id.nav_recipe -> startActivity(Intent(this, RecipeChatActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}