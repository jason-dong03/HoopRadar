package com.main.hoopradar


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.main.hoopradar.ui.theme.HoopRadarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HoopRadarTheme {
                HoopRadarApp()
            }
        }
    }
}