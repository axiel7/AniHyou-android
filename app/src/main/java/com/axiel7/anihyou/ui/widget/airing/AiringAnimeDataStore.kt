package com.axiel7.anihyou.ui.widget.airing

import android.content.Context
import androidx.datastore.core.DataStore
import com.axiel7.anihyou.AiringWidgetQuery
import com.axiel7.anihyou.common.GlobalVariables
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AiringAnimeDataStore(
    private val context: Context
) : DataStore<DataResult<List<AiringWidgetQuery.Medium>>> {
    override val data: Flow<DataResult<List<AiringWidgetQuery.Medium>>>
        get() {
            val appContext = context.applicationContext ?: throw IllegalStateException()
            val hiltEntryPoint =
                EntryPointAccessors.fromApplication(appContext, AiringAnimeEntryPoint::class.java)
            hiltEntryPoint.globalVariables.accessToken = runBlocking {
                hiltEntryPoint.defaultPreferencesRepository.accessToken.first()
            }
            return hiltEntryPoint.mediaRepository.getAiringWidgetData(page = 1, perPage = 50)
        }

    override suspend fun updateData(
        transform: suspend (t: DataResult<List<AiringWidgetQuery.Medium>>) -> DataResult<List<AiringWidgetQuery.Medium>>
    ): DataResult<List<AiringWidgetQuery.Medium>> {
        throw NotImplementedError()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AiringAnimeEntryPoint {
        val globalVariables: GlobalVariables
        val mediaRepository: MediaRepository
        val defaultPreferencesRepository: DefaultPreferencesRepository
    }
}