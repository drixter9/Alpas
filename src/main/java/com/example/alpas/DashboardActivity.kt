package com.example.alpas

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.alpas.firestore.FirestoreClass
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.handleCoroutineException

class DashboardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/${FirestoreClass().getCurrentUserID()}")

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_forum, R.id.navigation_shopping, R.id.navigation_consultation,R.id.navigation_notification,R.id.navigation_profile))
        navView.setupWithNavController(navController)
    }
    override fun onBackPressed() {
        doubelBackToExit()
    }

}