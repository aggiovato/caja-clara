package com.cajaclara.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cajaclara.app.ui.navigation.AppNavigation
import com.cajaclara.app.ui.theme.CajaClaraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CajaClaraTheme {
                AppNavigation()
            }
        }
    }
}
