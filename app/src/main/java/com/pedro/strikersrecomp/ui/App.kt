package com.pedro.strikersrecomp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.pedro.strikersrecomp.Store

private data class Tab(val label: String, val icon: String)

private val TABS = listOf(
    Tab("Jogar", "⚽"),
    Tab("Config", "⚙️"),
    Tab("Cheats", "⚡")
)

@Composable
fun StrikersApp(store: Store) {
    var tab by rememberSaveable { mutableIntStateOf(0) }

    Box(Modifier.fillMaxSize()) {
        Pitch()
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(containerColor = Turf.Ink.copy(alpha = 0.94f)) {
                    TABS.forEachIndexed { i, t ->
                        NavigationBarItem(
                            selected = tab == i,
                            onClick = { tab = i },
                            icon = { Text(t.icon, fontSize = 20.sp) },
                            label = { Text(t.label) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Turf.Orange.copy(alpha = 0.25f),
                                selectedTextColor = Turf.Orange,
                                unselectedTextColor = Turf.Chalk.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (tab) {
                    0 -> PlayScreen(store)
                    1 -> ConfigScreen(store)
                    else -> ToggleScreen("Cheats", "Ainda sem efeito: o motor nativo precisa ser integrado.", CHEATS, store, "cheat_")
                }
            }
        }
    }
}
