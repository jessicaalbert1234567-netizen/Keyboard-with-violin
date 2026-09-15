package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.settings.KeyboardInputMode
import com.example.data.settings.KeyboardSettings
import com.example.language.LanguagePack
import com.example.language.LanguagePackManager
import com.example.language.PackDownloadStatus

@Composable
fun LanguagesScreen(
    settings: KeyboardSettings,
    onUpdateSettings: (KeyboardSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val packManager = remember { LanguagePackManager.getInstance(context) }
    val allPacks by packManager.allPacksFlow.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredPacks = allPacks.filter {
        val matchesQuery = it.name.contains(searchQuery, ignoreCase = true) ||
                it.nativeName.contains(searchQuery, ignoreCase = true) ||
                it.id.contains(searchQuery, ignoreCase = true)
        val matchesTab = if (selectedTab == 0) {
            it.status == PackDownloadStatus.INSTALLED || it.status == PackDownloadStatus.DOWNLOADED
        } else {
            it.status == PackDownloadStatus.AVAILABLE || it.status == PackDownloadStatus.DOWNLOADING
        }
        matchesQuery && matchesTab
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Language Packs",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Top 50 global languages with offline dictionaries and transliteration.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Active Language & Mode Selector Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val currentPack = allPacks.find { it.id == settings.currentLanguageId }
                val displayLang = currentPack?.let { "${it.nativeName} (${it.name})" }
                    ?: if (settings.currentLanguageId == "bn") "বাংলা (Bengali)" else "English"
                Text(
                    text = "Current Typing Language: $displayLang",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onUpdateSettings(
                                settings.copy(
                                    currentLanguageId = "en",
                                    currentInputMode = KeyboardInputMode.ENGLISH
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = if (settings.currentLanguageId == "en") ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Text("English")
                    }

                    Button(
                        onClick = {
                            onUpdateSettings(
                                settings.copy(
                                    currentLanguageId = "bn",
                                    currentInputMode = KeyboardInputMode.NATIVE
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = if (settings.currentLanguageId == "bn" && settings.currentInputMode == KeyboardInputMode.NATIVE) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Text("বাংলা Native")
                    }

                    Button(
                        onClick = {
                            onUpdateSettings(
                                settings.copy(
                                    currentLanguageId = "bn",
                                    currentInputMode = KeyboardInputMode.PHONETIC
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = if (settings.currentLanguageId == "bn" && settings.currentInputMode == KeyboardInputMode.PHONETIC) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Text("বাংলা Phonetic")
                    }

                    if (settings.currentLanguageId != "en" && settings.currentLanguageId != "bn" && currentPack != null) {
                        Button(
                            onClick = {
                                onUpdateSettings(
                                    settings.copy(currentInputMode = KeyboardInputMode.NATIVE)
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = if (settings.currentInputMode == KeyboardInputMode.NATIVE) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text("${currentPack.nativeName} Native")
                        }

                        Button(
                            onClick = {
                                onUpdateSettings(
                                    settings.copy(currentInputMode = KeyboardInputMode.PHONETIC)
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = if (settings.currentInputMode == KeyboardInputMode.PHONETIC) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text("${currentPack.nativeName} Phonetic")
                        }
                    }
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search 50 languages...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("language_search_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Installed", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Available to Download", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold)
            }
        }

        // List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredPacks, key = { it.id }) { pack ->
                LanguagePackRow(
                    pack = pack,
                    isCurrent = settings.currentLanguageId == pack.id,
                    onSelect = {
                        val mode = when (pack.id) {
                            "en" -> KeyboardInputMode.ENGLISH
                            else -> KeyboardInputMode.NATIVE
                        }
                        onUpdateSettings(settings.copy(currentLanguageId = pack.id, currentInputMode = mode))
                    },
                    onDownload = { packManager.downloadPack(pack.id) },
                    onDelete = { packManager.deletePack(pack.id) }
                )
            }
        }
    }
}

@Composable
private fun LanguagePackRow(
    pack: LanguagePack,
    isCurrent: Boolean,
    onSelect: () -> Unit,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = pack.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "(${pack.nativeName})", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                Text(
                    text = "Size: ${pack.fileSizeFormatted} • Status: ${pack.status.name}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            when (pack.status) {
                PackDownloadStatus.INSTALLED, PackDownloadStatus.DOWNLOADED -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isCurrent) {
                            Text("Active ✓", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        } else {
                            Button(
                                onClick = onSelect,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Text("Use")
                            }
                        }
                        if (pack.id != "en" && pack.id != "bn") {
                            IconButton(onClick = onDelete) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Pack", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                PackDownloadStatus.DOWNLOADING -> {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
                PackDownloadStatus.AVAILABLE -> {
                    Button(
                        onClick = onDownload,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Get")
                    }
                }
                PackDownloadStatus.UPDATE_AVAILABLE -> {
                    Button(onClick = onDownload, shape = RoundedCornerShape(8.dp)) {
                        Text("Update")
                    }
                }
            }
        }
    }
}
