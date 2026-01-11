/*
 * Copyright 2023 The Android Open Source Project
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

package ru.akhilko.sync.initializers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import ru.akhilko.christian.calendar.sync.R

private const val SyncNotificationId = 0
private const val SyncNotificationChannelID = "SyncNotificationChannel"

/**
 * Foreground service notification for SyncWorker
 */
fun Context.SyncForegroundServiceNotification(): ForegroundInfo {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            SyncNotificationChannelID,
            getString(R.string.christian_calendar_sync_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.christian_calendar_sync_notification_channel_description)
        }
        notificationManager.createNotificationChannel(channel)
    }

    return ForegroundInfo(
        SyncNotificationId,
        NotificationCompat.Builder(this, SyncNotificationChannelID)
            .setSmallIcon(
                android.R.drawable.ic_popup_sync, // Используем системную иконку, чтобы избежать проблем с ресурсами
            )
            .setContentTitle(getString(R.string.christian_calendar_sync_notification_title))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build(),
    )
}
