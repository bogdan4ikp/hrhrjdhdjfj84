package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.LockScreenRepository
import com.example.ui.LockScreenViewModel
import com.example.ui.LockScreenViewModelFactory
import com.example.ui.LumenDashboardScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup edge-to-edge transparent systems bars to support immersive lock screens
        enableEdgeToEdge()

        // Initialize Room persistence layer components
        val database = AppDatabase.getDatabase(this)
        val repository = LockScreenRepository(database.lockScreenDao())
        
        // Factory construct the shared customizer and simulator state machine ViewModel
        val factory = LockScreenViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[LockScreenViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LumenDashboardScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
