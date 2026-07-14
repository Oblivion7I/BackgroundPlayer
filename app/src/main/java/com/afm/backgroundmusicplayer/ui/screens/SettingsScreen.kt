package com.afm.backgroundmusicplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.afm.backgroundmusicplayer.ui.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MusicViewModel
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isDynamicColor by viewModel.isDynamicColor.collectAsState()
    val autoResume by viewModel.autoResume.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Theme")
                Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleDarkMode() })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dynamic Wallpaper Colors")
                Switch(checked = isDynamicColor, onCheckedChange = { viewModel.toggleDynamicColor() })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Auto-Resume Last Played")
                Switch(checked = autoResume, onCheckedChange = { viewModel.toggleAutoResume() })
            }

            Spacer(modifier = Modifier.weight(1f))

            Text("AFM Player v1.0.0", style = MaterialTheme.typography.bodySmall)
            Text("Maintained by AFM Coders Lab", style = MaterialTheme.typography.bodySmall)
        }
    }
}