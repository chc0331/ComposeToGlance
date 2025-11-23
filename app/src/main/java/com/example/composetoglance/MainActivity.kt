package com.example.composetoglance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.composetoglance.ui.MainContent
import com.example.composetoglance.ui.theme.ComposeToGlanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeToGlanceTheme {
                MainContent()
            }
        }
    }
}
