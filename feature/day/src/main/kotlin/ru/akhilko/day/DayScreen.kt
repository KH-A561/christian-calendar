package ru.akhilko.day

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.akhilko.christian_calendar.core.model.CalendarDay
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingInfo
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.christian_calendar.core.model.LiturgicalColor
import ru.akhilko.christian_calendar.core.model.LiturgicalInfo

@Composable
fun DayScreen(uiState: DayScreenUiState) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is DayScreenUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is DayScreenUiState.Success -> {
                Text(text = uiState.day.title)
            }

            is DayScreenUiState.Error -> {
                Text(text = "Error", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayScreenLoadingPreview() {
    DayScreen(uiState = DayScreenUiState.Loading)
}

@Preview(showBackground = true)
@Composable
private fun DayScreenSuccessPreview() {
    val sampleDay = CalendarDay(
        gregorianDay = 1,
        gregorianMonth = 1,
        gregorianYear = 2024,
        julianDay = 19,
        julianMonth = 12,
        julianYear = 2023,
        dayTypes = listOf(DayType.FEAST),
        liturgicalInfo = LiturgicalInfo(
            importance = 3,
            color = LiturgicalColor.RED
        ),
        fastingInfo = FastingInfo(fastingLevel = FastingLevel.NONE, allowed = emptyList()),
        title = "Великий праздник",
        dayOfWeek = kotlinx.datetime.DayOfWeek.MONDAY,
        lastUpdated = "",
        week = "",
        readings = emptyList(),
        saints = emptyList(),
        searchText = ""
    )
    DayScreen(uiState = DayScreenUiState.Success(sampleDay))
}
