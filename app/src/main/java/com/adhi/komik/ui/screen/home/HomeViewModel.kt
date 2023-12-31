package com.adhi.komik.ui.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhi.komik.data.KomikRepository
import com.adhi.komik.model.Komik
import com.adhi.komik.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val komikRepository: KomikRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<List<Komik>>> = MutableStateFlow(UiState.Loading)
    val uiState get() = _uiState.asStateFlow()

    private val _query = mutableStateOf("")
    val query: State<String> get() = _query

    fun search(newQuery: String) = viewModelScope.launch {
        _query.value = newQuery
        komikRepository.searchKomikPlaces(_query.value)
            .catch {
                _uiState.value = UiState.Error(it.message.toString())
            }
            .collect {
                _uiState.value = UiState.Success(it)
            }
    }

    fun updateKomikPlace(id: Int, newState: Boolean) = viewModelScope.launch {
        komikRepository.updateKomikPlace(id, newState)
            .collect { isUpdated ->
                if (isUpdated) search(_query.value)
            }
    }
}