package io.ii.taskdecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.ii.presentation.navigation.AppNavHost
import io.ii.taskdecompose.ui.theme.TaskDecomposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskDecomposeTheme {
                AppNavHost()
            }
        }
    }
}
