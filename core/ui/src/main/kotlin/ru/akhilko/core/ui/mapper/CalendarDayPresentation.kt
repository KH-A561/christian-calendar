package ru.akhilko.core.ui.mapper

import ru.akhilko.christian_calendar.core.model.CalendarDay
import ru.akhilko.christian_calendar.core.model.DayType
import ru.akhilko.christian_calendar.core.model.FastingLevel
import ru.akhilko.core.designsystem.component.BadgeKind
import ru.akhilko.core.ui.format.monthYearRu
import ru.akhilko.core.ui.format.weekdayRu
import java.time.LocalDate

/**
 * UI-представление [CalendarDay]: всё, что нужно для рендеринга экранов
 * Day/Week/Month, без обращения к доменной модели в Composable.
 *
 * Единственный источник правды для:
 *  - деривации [BadgeKind] из dayTypes (см. ограничение #6 плана);
 *  - русской локализации даты/дня недели/месяца.
 */
data class CalendarDayPresentation(
    val id: String,
    val gregorianDayNum: Int,
    val gregorianMonth: Int,
    val gregorianYear: Int,
    val julianDay: Int,
    val julianMonth: Int,
    val julianYear: Int,
    val monthYearRu: String,
    val weekdayRu: String,
    val weekdayShortRu: String,
    val badges: List<BadgeKind>,
    val title: String,
    val weekText: String?,
    val fastingLevelRu: String,
    val fastingName: String?,
    val allowed: List<String>,
    val readings: List<String>,
    val saints: List<String>,
    val isToday: Boolean,
) {
    val isFast: Boolean get() = BadgeKind.FAST in badges
    val isFeast: Boolean get() = BadgeKind.FEAST in badges || BadgeKind.GREAT in badges
    val isGreat: Boolean get() = BadgeKind.GREAT in badges
    val isRemembrance: Boolean get() = BadgeKind.REMEMBRANCE in badges
}

fun CalendarDay.toPresentation(today: LocalDate = LocalDate.now()): CalendarDayPresentation {
    val badges = deriveBadges(dayTypes, fastingInfo.fastingLevel)
    val gregorian = LocalDate.of(gregorianYear, gregorianMonth, gregorianDay)
    return CalendarDayPresentation(
        id = id,
        gregorianDayNum = gregorianDay,
        gregorianMonth = gregorianMonth,
        gregorianYear = gregorianYear,
        julianDay = julianDay,
        julianMonth = julianMonth,
        julianYear = julianYear,
        monthYearRu = monthYearRu(gregorianMonth, gregorianYear),
        weekdayRu = weekdayRu(dayOfWeek),
        weekdayShortRu = weekdayRu(dayOfWeek, short = true),
        badges = badges,
        title = title,
        weekText = week.takeIf { it.isNotBlank() },
        fastingLevelRu = fastingLevelRu(fastingInfo.fastingLevel),
        fastingName = fastingInfo.fastingName?.takeIf { it.isNotBlank() },
        allowed = fastingInfo.allowed,
        readings = readings,
        saints = saints,
        isToday = gregorian == today,
    )
}

private fun deriveBadges(
    dayTypes: List<DayType>,
    fastingLevel: FastingLevel,
): List<BadgeKind> {
    val result = mutableListOf<BadgeKind>()
    val isEaster = dayTypes.contains(DayType.EASTER)
    val isTwelve = dayTypes.contains(DayType.TWELVE_GREAT_FEASTS)
    val isGreat = dayTypes.contains(DayType.GREAT_FEAST)
    val isFeast = dayTypes.contains(DayType.FEAST)
    val isRemembrance = dayTypes.contains(DayType.COMMEMORATION)
    val isFast = dayTypes.contains(DayType.LONG_FAST) ||
            dayTypes.contains(DayType.FAST) ||
            fastingLevel != FastingLevel.NONE

    if (isEaster) result += BadgeKind.EASTER
    if (isTwelve) result += BadgeKind.TWELVE
    if (isGreat) result += BadgeKind.GREAT
    if (isFeast) result += BadgeKind.FEAST
    if (isRemembrance) result += BadgeKind.REMEMBRANCE
    if (isFast) result += BadgeKind.FAST
    return result
}

fun fastingLevelRu(level: FastingLevel): String = when (level) {
    FastingLevel.STRICT -> "Строгий пост"
    FastingLevel.PARTIAL -> "Частичный пост"
    FastingLevel.NONE -> "Поста нет"
}
