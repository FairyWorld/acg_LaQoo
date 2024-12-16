package com.laqoome.laqoo.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laqoome.laqoo.domain.model.Home
import com.laqoome.laqoo.domain.repository.laqooRepository
import com.laqoome.laqoo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: laqooRepository
) : ViewModel() {
    private val _homeDataList: MutableStateFlow<Resource<List<Home>>> =
        MutableStateFlow(value = Resource.Loading)
    val homeDataList: StateFlow<Resource<List<Home>>>
        get() = _homeDataList

    init {
        getHomeData()
    }

    private fun getHomeData() {
        viewModelScope.launch {
            _homeDataList.value = repository.getHomeData()
        }
    }

    fun refresh() {
        _homeDataList.value = Resource.Loading
        getHomeData()
    }
}