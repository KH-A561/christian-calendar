package ru.akhilko.christian_calendar

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChristianCalendarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}