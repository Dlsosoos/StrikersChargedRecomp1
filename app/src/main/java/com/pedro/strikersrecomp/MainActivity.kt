package com.pedro.strikersrecomp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.pedro.strikersrecomp.ui.StrikersApp
import com.pedro.strikersrecomp.ui.StrikersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Guarda o erro de um crash para mostrar na próxima abertura do app.
        val crashPrefs = getSharedPreferences("crash", MODE_PRIVATE)
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            crashPrefs.edit().putString("last", Log.getStackTraceString(error)).commit()
            previous?.uncaughtException(thread, error)
        }

        val store = Store(applicationContext)
        setContent {
            StrikersTheme {
                StrikersApp(store)

                var crash by remember { mutableStateOf(crashPrefs.getString("last", null)) }
                if (crash != null) {
                    AlertDialog(
                        onDismissRequest = {},
                        confirmButton = {
                            TextButton(onClick = {
                                crashPrefs.edit().remove("last").apply()
                                crash = null
                            }) { Text("Fechar") }
                        },
                        title = { Text("O app fechou da última vez") },
                        text = {
                            Text(
                                crash!!.take(1800),
                                fontSize = 11.sp,
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            )
                        }
                    )
                }
            }
        }
    }
}
