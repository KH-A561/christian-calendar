package ru.akhilko.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarViewMonth
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsDrawerContent(
    isDarkTheme: Boolean,
    isSyncing: Boolean,
    onOpenYear: () -> Unit,
    onToggleTheme: () -> Unit,
    onTriggerSync: () -> Unit,
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.titleLarge,
        )

        NavigationDrawerItem(
            label = { Text("Год") },
            selected = false,
            onClick = onOpenYear,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarViewMonth,
                    contentDescription = null,
                )
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledIconButton(
                onClick = onToggleTheme,
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                    contentDescription = if (isDarkTheme) "Темная тема" else "Светлая тема",
                )
            }

            FilledIconButton(
                onClick = onTriggerSync,
                enabled = !isSyncing,
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
            ) {
                if (isSyncing) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Синхронизация",
                    )
                }
            }
        }
    }
}
