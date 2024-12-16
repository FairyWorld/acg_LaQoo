package com.laqoome.laqoo.presentation.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.laqoome.laqoo.domain.model.laqooDetail
import com.laqoome.laqoo.domain.model.Download
import com.laqoome.laqoo.domain.model.Episode
import com.laqoome.laqoo.domain.model.Favourite
import com.laqoome.laqoo.domain.model.History
import com.laqoome.laqoo.domain.repository.laqooRepository
import com.laqoome.laqoo.domain.repository.RoomRepository
import com.laqoome.laqoo.domain.usecase.GetlaqooDetailUseCase
import com.laqoome.laqoo.presentation.navigation.Screen
import com.laqoome.laqoo.util.Resource
import com.laqoome.laqoo.util.SourceMode
import com.laqoome.laqoo.util.onSuccess
import com.laqoome.download.download
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class laqooDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val laqooRepository: laqooRepository,
    private val roomRepository: RoomRepository,
    private val getlaqooDetailUseCase: GetlaqooDetailUseCase
) : ViewModel() {

    private val _laqooDetailState: MutableStateFlow<Resource<laqooDetail?>> =
        MutableStateFlow(value = Resource.Loading)
    val laqooDetailState: StateFlow<Resource<laqooDetail?>>
        get() = _laqooDetailState

    private val _isFavourite: MutableStateFlow<Boolean> =
        MutableStateFlow(value = false)
    val isFavourite: StateFlow<Boolean>
        get() = _isFavourite

    lateinit var detailUrl: String
    lateinit var mode: SourceMode

    init {
        savedStateHandle.toRoute<Screen.laqooDetail>().let {
            this.mode = it.mode
            this.detailUrl = it.detailUrl
            getlaqooDetail(this.detailUrl)
        }
    }

    private fun getlaqooDetail(detailUrl: String) {
        viewModelScope.launch {
            _isFavourite.value = roomRepository.checkFavourite(detailUrl).first()
            getlaqooDetailUseCase(detailUrl, mode).collect {
                _laqooDetailState.value = it
            }
        }
    }

    fun favourite(favourite: Favourite) {
        viewModelScope.launch {
            _isFavourite.value = !_isFavourite.value
            roomRepository.addOrRemoveFavourite(favourite)
        }
    }

    fun addHistory(history: History) {
        viewModelScope.launch {
            roomRepository.addHistory(history)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun addDownload(download: Download, episodeUrl: String, file: File) {
        viewModelScope.launch {
            laqooRepository.getVideoData(episodeUrl, mode)
                .onSuccess {
                    val videoUrl = it.url
                    val downloadDetail =
                        download.downloadDetails.first().copy(downloadUrl = videoUrl)
                    roomRepository.addDownload(download.copy(downloadDetails = listOf(downloadDetail)))
                    // 开始下载视频
                    GlobalScope.download(videoUrl, saveName = file.name, savePath = file.parent!!)
                        .start()
                }
        }
    }

    fun handleDownloadedEpisode(episodes: List<Episode>): Flow<List<Episode>> {
        return flow {
            roomRepository.checkDownload(detailUrl).collect { isStoredDownload ->
                if (!isStoredDownload) {
                    emit(episodes)
                } else {
                    roomRepository.getDownloadDetails(detailUrl).collect { downloadedEpisodes ->
                        val episodeList = episodes.map { episode ->

                            val downloadIndex =
                                downloadedEpisodes.indexOfFirst { d -> d.title == episode.name }

                            episode.copy(isDownloaded = downloadIndex != -1)
                        }

                        emit(episodeList)
                    }
                }
            }
        }
    }

    fun retry() {
        _laqooDetailState.value = Resource.Loading
        getlaqooDetail(this.detailUrl)
    }

    fun onChannelClick(index: Int, episodes: List<Episode>) {
        _laqooDetailState.value = Resource.Success(
            _laqooDetailState.value.data!!.copy(
                channelIndex = index,
                episodes = episodes
            )
        )
    }
}