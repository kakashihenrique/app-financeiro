package com.fincalc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fincalc.app.ui.navigation.FinCalcNavHost
import com.fincalc.app.ui.theme.FinCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FinCalcTheme {
                FinCalcNavHost()
            }
        }
    }
}
