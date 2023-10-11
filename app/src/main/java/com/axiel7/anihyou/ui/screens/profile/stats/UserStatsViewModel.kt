package com.axiel7.anihyou.ui.screens.profile.stats

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.UserStatsAnimeOverviewQuery
import com.axiel7.anihyou.UserStatsMangaOverviewQuery
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.stats.FormatDistribution
import com.axiel7.anihyou.data.model.stats.ScoreDistribution
import com.axiel7.anihyou.data.model.stats.Stat
import com.axiel7.anihyou.data.model.stats.StatLocalizableAndColorable
import com.axiel7.anihyou.data.model.stats.StatusDistribution
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.screens.profile.USER_ID_ARGUMENT
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class UserStatsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

    //TODO: user stats

    val userId: Int = savedStateHandle[USER_ID_ARGUMENT.removeFirstAndLast()]!!

    var isLoading by mutableStateOf(true)
        private set

    var statType by mutableStateOf(UserStatType.OVERVIEW)
    var mediaType by mutableStateOf(MediaType.ANIME)
    val isAnime by derivedStateOf { mediaType == MediaType.ANIME }
    var scoreCountType by mutableStateOf(ScoreStatCountType.TITLES)

    var animeOverview by mutableStateOf<UserStatsAnimeOverviewQuery.Anime?>(null)
    val plannedAnime by derivedStateOf { animeOverview?.statuses?.find { it?.status == MediaListStatus.PLANNING } }
    val animeScoreStatsCount = mutableStateListOf<Stat<ScoreDistribution>>()
    val animeScoreStatsTime = mutableStateListOf<Stat<ScoreDistribution>>()
    val animeStatusDistribution = mutableStateListOf<Stat<StatusDistribution>>()
    val animeFormatDistribution = mutableStateListOf<Stat<FormatDistribution>>()

    var mangaOverview by mutableStateOf<UserStatsMangaOverviewQuery.Manga?>(null)
    val plannedManga by derivedStateOf { mangaOverview?.statuses?.find { it?.status == MediaListStatus.PLANNING } }
    val mangaScoreStatsCount = mutableStateListOf<Stat<ScoreDistribution>>()
    val mangaScoreStatsTime = mutableStateListOf<Stat<ScoreDistribution>>()
    val mangaStatusDistribution = mutableStateListOf<Stat<StatusDistribution>>()
    val mangaFormatDistribution = mutableStateListOf<Stat<FormatDistribution>>()

    fun getOverview() = viewModelScope.launch {
        isLoading = true
        if (mediaType == MediaType.ANIME) {
            userRepository.getOverviewAnimeStats(userId).collect { result ->
                if (result is DataResult.Success) {
                    animeOverview = result.data
                    animeScoreStatsCount.clear()
                    animeScoreStatsTime.clear()
                    animeStatusDistribution.clear()
                    animeFormatDistribution.clear()
                    result.data?.scores?.filterNotNull()?.forEach { scoreStat ->
                        animeScoreStatsCount.add(
                            StatLocalizableAndColorable(
                                type = ScoreDistribution(score = scoreStat.meanScore.roundToInt()),
                                value = scoreStat.count.toFloat()
                            )
                        )
                        animeScoreStatsTime.add(
                            StatLocalizableAndColorable(
                                type = ScoreDistribution(score = scoreStat.meanScore.roundToInt()),
                                value = scoreStat.minutesWatched.toFloat()
                            )
                        )
                    }
                    result.data?.statuses?.filterNotNull()?.forEach { statusStat ->
                        val status = StatusDistribution.valueOf(statusStat.status?.rawValue)
                        if (status != null) {
                            animeStatusDistribution.add(
                                StatLocalizableAndColorable(
                                    type = status,
                                    value = statusStat.count.toFloat()
                                )
                            )
                        }
                    }
                    result.data?.formats?.filterNotNull()?.forEach { formatStat ->
                        val format = FormatDistribution.valueOf(formatStat.format?.rawValue)
                        if (format != null) {
                            animeFormatDistribution.add(
                                StatLocalizableAndColorable(
                                    type = format,
                                    value = formatStat.count.toFloat()
                                )
                            )
                        }
                    }
                } else if (result is DataResult.Error) {
                    //message = result.message
                }
            }
        } else if (mediaType == MediaType.MANGA) {
            userRepository.getOverviewMangaStats(userId).collect { result ->
                if (result is DataResult.Success) {
                    mangaScoreStatsCount.clear()
                    mangaScoreStatsTime.clear()
                    mangaStatusDistribution.clear()
                    mangaFormatDistribution.clear()
                    mangaOverview = result.data
                    result.data?.scores?.filterNotNull()?.forEach { scoreStat ->
                        mangaScoreStatsCount.add(
                            StatLocalizableAndColorable(
                                type = ScoreDistribution(score = scoreStat.meanScore.roundToInt()),
                                value = scoreStat.count.toFloat()
                            )
                        )
                        mangaScoreStatsTime.add(
                            StatLocalizableAndColorable(
                                type = ScoreDistribution(score = scoreStat.meanScore.roundToInt()),
                                value = scoreStat.chaptersRead.toFloat()
                            )
                        )
                    }
                    result.data?.statuses?.filterNotNull()?.forEach { statusStat ->
                        val status = StatusDistribution.valueOf(statusStat.status?.rawValue)
                        if (status != null) {
                            mangaStatusDistribution.add(
                                StatLocalizableAndColorable(
                                    type = status,
                                    value = statusStat.count.toFloat()
                                )
                            )
                        }
                    }
                    result.data?.formats?.filterNotNull()?.forEach { formatStat ->
                        val format = FormatDistribution.valueOf(formatStat.format?.rawValue)
                        if (format != null) {
                            mangaFormatDistribution.add(
                                StatLocalizableAndColorable(
                                    type = format,
                                    value = formatStat.count.toFloat()
                                )
                            )
                        }
                    }
                } else if (result is DataResult.Error) {
                    //message = result.message
                }
            }
        }
        isLoading = false
    }
}