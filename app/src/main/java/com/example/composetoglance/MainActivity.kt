package com.example.composetoglance

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetoglance.ui.theme.ComposeToGlanceTheme
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val logger = Logger()
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue)
                    .provideLogger(logger)
                    .semantics {
                        this[SemanticsPropertyKey(name = "parent")] = 0
                    }
                    .track("Box1")
            ) {

                Column(
                    modifier = Modifier
                        .size(100.dp)
                        .track("Column1")
                        .provideLogger(logger)
                ) {
                    var show by remember { mutableStateOf(false) }
                    Button(onClick = {
                        show = !show
                    }) {
                        Text("Button", modifier = Modifier.track("ButtonText"))
                    }
                    if (show)
                        Text(text = "Test", modifier = Modifier.track("ShowText"))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}