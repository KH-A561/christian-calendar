package ru.akhilko.month.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import ru.akhilko.core.ui.format.monthNominativeRu
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthTopBar(
    visibleMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSearchClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onPrevMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Предыдущий месяц",
                )
            }
        },
        title = {
            Text(
                text = "${monthNominativeRu(visibleMonth.monthValue)} ${visibleMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Следующий месяц",
                )
            }
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск",
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
        ),
    )
}
