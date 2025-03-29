package ru.akhilko.christian_calendar.core.data.model.search

interface Searchable {
    fun getSearchableTextFieldNames() : List<String>
}