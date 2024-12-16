package com.laqoome.laqoo.domain.usecase

import com.laqoome.laqoo.domain.model.laqooDetail
import com.laqoome.laqoo.domain.model.Episode
import com.laqoome.laqoo.domain.repository.laqooRepository
import com.laqoome.laqoo.domain.repository.RoomRepository
import com.laqoome.laqoo.util.Resource
import com.laqoome.laqoo.util.SourceMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetlaqooDetailUseCase @Inject constructor(
    private val laqooRepository: laqooRepository,
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(detailUrl: String, mode: SourceMode): Flow<Resource<laqooDetail?>> {
        return flow {
            when (val resource = laqooRepository.getlaqooDetail(detailUrl, mode)) {
                is Resource.Error -> emit(Resource.Error(error = resource.error))
                is Resource.Loading -> emit(Resource.Loading)
                is Resource.Success -> {
                    try {
                        roomRepository.checkHistory(detailUrl).collect { isStoredHistory ->
                            if (!isStoredHistory) {
                                emit(Resource.Success(data = resource.data))
                            } else {
                                roomRepository.getEpisodes(detailUrl).collect { localEpisodes ->
                                    if (localEpisodes.isEmpty()) {
                                        emit(Resource.Success(data = resource.data))
                                    } else {
                                        val lastPlayedEpisode = localEpisodes.first()
                                        val remoteEpisodes = resource.data!!.episodes

                                        val lastPosition =
                                            remoteEpisodes.indexOfFirst { it.url == lastPlayedEpisode.url }
                                        val episodeList = remoteEpisodes.map { episode ->
                                            val index =
                                                localEpisodes.indexOfFirst { e -> e.url == episode.url }
                                            Episode(
                                                name = episode.name,
                                                url = episode.url,
                                                lastPlayPosition = if (index != -1) localEpisodes[index].lastPlayPosition else 0L,
                                                isPlayed = index != -1
                                            )
                                        }

                                        emit(
                                            Resource.Success(
                                                data = resource.data.copy(
                                                    lastPosition = lastPosition.coerceAtLeast(0),
                                                    episodes = episodeList
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        emit(Resource.Error(error = e))
                    }
                }
            }
        }
    }
}