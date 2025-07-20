package ru.akhilko.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.akhilko.christian_calendar.core.data.model.search.SearchResult
import ru.akhilko.core.database.repository.SearchContentsRepository
import ru.akhilko.core.domain.GetSearchContentsUseCase
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    getSearchContentsUseCase: GetSearchContentsUseCase,
    private val searchContentsRepository: SearchContentsRepository,
    private val savedStateHandle: SavedStateHandle,
//    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val searchResultUiState: StateFlow<SearchResultUiState> =
        searchContentsRepository.getSearchContentsCount()
            .flatMapLatest { totalCount ->
                if (totalCount < SEARCH_MIN_FTS_ENTITY_COUNT) {
                    flowOf(SearchResultUiState.SearchNotReady)
                } else {
                    searchQuery.flatMapLatest { query ->
                        if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                            flowOf(SearchResultUiState.EmptyQuery)
                        } else {
                            getSearchContentsUseCase(query)
                                // Not using .asResult() here, because it emits Loading state every
                                // time the user types a letter in the search box, which flickers the screen.
                                .map<SearchResult, SearchResultUiState> { data ->
                                    SearchResultUiState.Success(
                                        results = data,
                                    )
                                }
                                .catch { emit(SearchResultUiState.LoadFailed) }
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SearchResultUiState.Loading,
            )

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }
}

/** Minimum length where search query is considered as [SearchResultUiState.EmptyQuery] */
private const val SEARCH_QUERY_MIN_LENGTH = 2

/** Minimum number of the fts table's entity count where it's considered as search is not ready */
private const val SEARCH_MIN_FTS_ENTITY_COUNT = 1
private const val SEARCH_QUERY = "searchQuery"