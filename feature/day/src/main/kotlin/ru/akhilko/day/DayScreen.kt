package ru.akhilko.day

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.component.BadgeKind
import ru.akhilko.core.designsystem.theme.CalendarTheme
import ru.akhilko.core.ui.mapper.CalendarDayPresentation
import ru.akhilko.day.ui.DayHero
import ru.akhilko.day.ui.DayTab
import ru.akhilko.day.ui.DayTabs
import ru.akhilko.day.ui.DayTopBar
import ru.akhilko.day.ui.tab.FastTabContent
import ru.akhilko.day.ui.tab.GeneralTabContent
import ru.akhilko.day.ui.tab.ReadingsTabContent
import ru.akhilko.day.ui.tab.SaintsTabContent
import java.time.LocalDate
import androidx.compose.material.icons.Icons as MaterialIcons

@Composable
fun DayScreen(
    uiState: DayScreenUiState,
    onBack: () -> Unit,
    onNavigateToDay: (String) -> Unit,
) {
    when (uiState) {
        DayScreenUiState.Loading -> CenteredLoading()
        DayScreenUiState.Error -> CenteredError("Не удалось загрузить день")
        is DayScreenUiState.Success -> DaySuccessContent(
            state = uiState,
            onBack = onBack,
            onNavigateToDay = onNavigateToDay,
        )
    }
}

@Composable
private fun DaySuccessContent(
    state: DayScreenUiState.Success,
    onBack: () -> Unit,
    onNavigateToDay: (String) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(DayTab.GENERAL) }
    Scaffold(
        topBar = {
            DayTopBar(
                title = state.day.monthYearRu,
                onBack = onBack,
                onPrev = { onNavigateToDay(state.prevId) },
                onNext = { onNavigateToDay(state.nextId) },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !state.day.isToday,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateToDay(LocalDate.now().toString()) },
                    icon = {
                        Icon(
                            imageVector = MaterialIcons.Default.Today,
                            contentDescription = null,
                        )
                    },
                    text = { Text("К сегодня") },
                )
            }
        },
        containerColor = Color.Transparent,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            DayHero(presentation = state.day)
            Spacer(Modifier.height(12.dp))
            DayTabs(selected = selectedTab, onSelect = { selectedTab = it })
            AnimatedContent(
                targetState = selectedTab,
                label = "DayTabContent",
                modifier = Modifier.fillMaxSize(),
            ) { tab ->
                when (tab) {
                    DayTab.GENERAL -> GeneralTabContent(state.day)
                    DayTab.FAST -> FastTabContent(state.day)
                    DayTab.READINGS -> ReadingsTabContent(state.day)
                    DayTab.SAINTS -> SaintsTabContent(state.day)
                }
            }
        }
    }
}

@Composable
private fun CenteredLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CenteredError(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message)
    }
}

@Preview(name = "Success - Light", group = "Success")
@Preview(name = "Success - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, group = "Success")
@Composable
private fun DayScreenSuccessPreview() {
    CalendarTheme {
        Surface {
            DayScreen(
                uiState = DayScreenUiState.Success(
                    day = previewDay(
                        title = "День Святого Духа",
                        badges = listOf(BadgeKind.GREAT, BadgeKind.FAST)
                    ),
                    prevId = "prev",
                    nextId = "next"
                ),
                onBack = {},
                onNavigateToDay = {}
            )
        }
    }
}

@Preview(name = "Success - Minimal", group = "Success")
@Composable
private fun DayScreenMinimalPreview() {
    CalendarTheme {
        Surface {
            DayScreen(
                uiState = DayScreenUiState.Success(
                    day = previewDay(
                        title = "Обычный день",
                        badges = emptyList(),
                        weekText = null,
                        fastingName = null
                    ),
                    prevId = "prev",
                    nextId = "next"
                ),
                onBack = {},
                onNavigateToDay = {}
            )
        }
    }
}

@Preview(name = "Loading - Light", group = "States")
@Preview(name = "Loading - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, group = "States")
@Composable
private fun DayScreenLoadingPreview() {
    CalendarTheme {
        Surface {
            DayScreen(
                uiState = DayScreenUiState.Loading,
                onBack = {},
                onNavigateToDay = {}
            )
        }
    }
}

@Preview(name = "Error - Light", group = "States")
@Preview(name = "Error - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, group = "States")
@Composable
private fun DayScreenErrorPreview() {
    CalendarTheme {
        Surface {
            DayScreen(
                uiState = DayScreenUiState.Error,
                onBack = {},
                onNavigateToDay = {}
            )
        }
    }
}

private fun previewDay(
    title: String,
    badges: List<BadgeKind> = emptyList(),
    weekText: String? = "Седмица 1-я по Пятидесятнице",
    fastingName: String? = "Троицкая седмица",
) = CalendarDayPresentation(
    id = "2024-05-24",
    gregorianDayNum = 24,
    gregorianMonth = 5,
    gregorianYear = 2024,
    julianDay = 11,
    julianMonth = 5,
    julianYear = 2024,
    monthYearRu = "Май 2024",
    weekdayRu = "Пятница",
    weekdayShortRu = "Пт",
    badges = badges,
    title = title,
    weekText = weekText,
    fastingLevelRu = "Пост",
    fastingName = fastingName,
    allowed = listOf("Рыба"),
    readings = listOf("Мф. 18:10-20"),
    saints = listOf("Святитель Николай"),
    isToday = false
)
