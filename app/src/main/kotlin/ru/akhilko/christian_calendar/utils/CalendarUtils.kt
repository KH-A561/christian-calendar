package ru.akhilko.christian_calendar.utils

import kotlinx.serialization.json.Json
import ru.akhilko.christian_calendar.model.Day

private val JSON = "{\n" +
        "  \"calendarDate\": {\n" +
        "    \"gregorianDate\": \"2024-05-07\",\n" +
        "    \"julianDate\": \"2024-04-24\",\n" +
        "    \"dayOfWeek\": \"Tuesday\"\n" +
        "  },\n" +
        "  \"title\": \"Вторник Светлой седмицы\",\n" +
        "  \"types\": [\n" +
        "    \"FEAST\",\n" +
        "    \"FAST\"\n" +
        "  ],\n" +
        "  \"details\": [\n" +
        "    \"Иверской иконы Божией Матери (переходящее празднование во вторник Светлой седмицы)\",\n" +
        "    \"Мч. Саввы Стратилата и с ним 70-ти воинов, Римских (272).\"\n" +
        "  ],\n" +
        "  \"readings\": {}\n" +
        "}"

fun readDayFromJson(): Day {
    return Json.decodeFromString<Day>(JSON)
}

fun main() {
    println(readDayFromJson())
}