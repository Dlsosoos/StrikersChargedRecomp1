package com.pedro.strikersrecomp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pedro.strikersrecomp.ui.StrikersApp
import com.pedro.strikersrecomp.ui.StrikersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = Store(applicationContext)
        setContent {
            StrikersTheme {
                StrikersApp(store)
            }
        }
    }
}
