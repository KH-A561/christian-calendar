/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.akhilko.christian_calendar.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import ru.akhilko.core.designsystem.icon.Icons
import ru.akhilko.feature.day.R as dayR
import ru.akhilko.feature.month.R as monthR
import ru.akhilko.feature.week.R as weekR

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    MONTH(
        selectedIcon = Icons.CalendarMonth,
        unselectedIcon = Icons.OutlinedCalendarMonth,
        iconTextId = monthR.string.feature_month_title,
        titleTextId = monthR.string.feature_month_title,
    ),
    WEEK(
        selectedIcon = Icons.ViewWeek,
        unselectedIcon = Icons.OutlinedViewWeek,
        iconTextId = weekR.string.feature_week_title,
        titleTextId = weekR.string.feature_week_title,
    ),
    DAY(
        selectedIcon = Icons.ViewDay,
        unselectedIcon = Icons.OutlinedViewDay,
        iconTextId = dayR.string.feature_day_title,
        titleTextId = dayR.string.feature_day_title,
    ),
}
