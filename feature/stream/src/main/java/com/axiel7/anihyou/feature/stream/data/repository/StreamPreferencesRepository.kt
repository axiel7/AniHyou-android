package com.axiel7.anihyou.feature.stream.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.axiel7.anihyou.feature.stream.data.model.AudioType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Persists all user streaming preferences and per-episode progress/notes.
 *
 * Keys stored in the app's single DataStore<Preferences> instance:
 *   stream_default_provider     — e.g. "kiwi"
 *   stream_audio_type           — "sub" | "dub"
 *   stream_quality              — "1080p" | "720p" | "480p" | "360p" | "auto"
 *   stream_auto_play            — Boolean
 *   stream_auto_next            — Boolean
 *   stream_auto_skip_intro      — Boolean
 *   stream_auto_skip_outro      — Boolean
 *   stream_api_base_url         — custom self-hosted API URL
 *   stream_ep_progress_{id}     — JSON: {"ep": N, "pos": seconds}
 *   stream_ep_notes_{id}_{ep}   — plain text note
 *   stream_watched_{id}         — JSON array of episode numbers
 */
class StreamPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    private val json = Json { ignoreUnknownKeys = true }

    // ── Playback preferences ──────────────────────────────────────────────────

    val defaultProvider: Flow<String> = dataStore.data.map {
        it[PREF_DEFAULT_PROVIDER] ?: DEFAULT_PROVIDER
    }

    suspend fun setDefaultProvider(provider: String) {
        dataStore.edit { it[PREF_DEFAULT_PROVIDER] = provider }
    }

    val audioType: Flow<AudioType> = dataStore.data.map {
        AudioType.from(it[PREF_AUDIO_TYPE] ?: AudioType.SUB.value)
    }

    suspend fun setAudioType(type: AudioType) {
        dataStore.edit { it[PREF_AUDIO_TYPE] = type.value }
    }

    val preferredQuality: Flow<String> = dataStore.data.map {
        it[PREF_QUALITY] ?: "auto"
    }

    suspend fun setPreferredQuality(quality: String) {
        dataStore.edit { it[PREF_QUALITY] = quality }
    }

    val autoPlay: Flow<Boolean> = dataStore.data.map { it[PREF_AUTO_PLAY] ?: true }
    suspend fun setAutoPlay(value: Boolean) { dataStore.edit { it[PREF_AUTO_PLAY] = value } }

    val autoNext: Flow<Boolean> = dataStore.data.map { it[PREF_AUTO_NEXT] ?: true }
    suspend fun setAutoNext(value: Boolean) { dataStore.edit { it[PREF_AUTO_NEXT] = value } }

    val autoSkipIntro: Flow<Boolean> = dataStore.data.map { it[PREF_AUTO_SKIP_INTRO] ?: false }
    suspend fun setAutoSkipIntro(value: Boolean) { dataStore.edit { it[PREF_AUTO_SKIP_INTRO] = value } }

    val autoSkipOutro: Flow<Boolean> = dataStore.data.map { it[PREF_AUTO_SKIP_OUTRO] ?: false }
    suspend fun setAutoSkipOutro(value: Boolean) { dataStore.edit { it[PREF_AUTO_SKIP_OUTRO] = value } }

    val apiBaseUrl: Flow<String> = dataStore.data.map {
        it[PREF_API_BASE_URL] ?: DEFAULT_API_URL
    }

    suspend fun setApiBaseUrl(url: String) {
        dataStore.edit { it[PREF_API_BASE_URL] = url.trimEnd('/') }
    }

    // ── Per-anime playback progress ───────────────────────────────────────────

    /** Save which episode number and position (seconds) the user is on for an anime. */
    suspend fun saveProgress(animeId: Int, episodeNumber: Int, positionSeconds: Long) {
        val key = progressKey(animeId)
        val value = """{"ep":$episodeNumber,"pos":$positionSeconds}"""
        dataStore.edit { it[key] = value }
    }

    /** Returns (episodeNumber, positionSeconds) or null if no progress saved. */
    fun getProgress(animeId: Int): Flow<Pair<Int, Long>?> {
        val key = progressKey(animeId)
        return dataStore.data.map { prefs ->
            prefs[key]?.let { raw ->
                runCatching {
                    val obj = json.parseToJsonElement(raw) as? kotlinx.serialization.json.JsonObject
                    val ep = obj?.get("ep")?.toString()?.toIntOrNull() ?: return@runCatching null
                    val pos = obj.get("pos")?.toString()?.toLongOrNull() ?: 0L
                    ep to pos
                }.getOrNull()
            }
        }
    }

    // ── Per-episode notes ─────────────────────────────────────────────────────

    suspend fun saveNote(animeId: Int, episodeNumber: Int, note: String) {
        dataStore.edit { it[noteKey(animeId, episodeNumber)] = note }
    }

    fun getNote(animeId: Int, episodeNumber: Int): Flow<String> =
        dataStore.data.map { it[noteKey(animeId, episodeNumber)] ?: "" }

    suspend fun deleteNote(animeId: Int, episodeNumber: Int) {
        dataStore.edit { it.remove(noteKey(animeId, episodeNumber)) }
    }

    // ── Watched episodes ──────────────────────────────────────────────────────

    fun getWatchedEpisodes(animeId: Int): Flow<Set<Int>> {
        val key = watchedKey(animeId)
        return dataStore.data.map { prefs ->
            prefs[key]?.split(",")?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        }
    }

    suspend fun markEpisodeWatched(animeId: Int, episodeNumber: Int) {
        val key = watchedKey(animeId)
        dataStore.edit { prefs ->
            val current = prefs[key]?.split(",")
                ?.mapNotNull { it.toIntOrNull() }
                ?.toMutableSet() ?: mutableSetOf()
            current.add(episodeNumber)
            prefs[key] = current.joinToString(",")
        }
    }

    suspend fun markEpisodeUnwatched(animeId: Int, episodeNumber: Int) {
        val key = watchedKey(animeId)
        dataStore.edit { prefs ->
            val current = prefs[key]?.split(",")
                ?.mapNotNull { it.toIntOrNull() }
                ?.toMutableSet() ?: mutableSetOf()
            current.remove(episodeNumber)
            prefs[key] = current.joinToString(",")
        }
    }

    // ── Key factories ─────────────────────────────────────────────────────────

    private fun progressKey(animeId: Int) = stringPreferencesKey("stream_ep_progress_$animeId")
    private fun noteKey(animeId: Int, ep: Int) = stringPreferencesKey("stream_ep_notes_${animeId}_$ep")
    private fun watchedKey(animeId: Int) = stringPreferencesKey("stream_watched_$animeId")

    companion object {
        private val PREF_DEFAULT_PROVIDER = stringPreferencesKey("stream_default_provider")
        private val PREF_AUDIO_TYPE = stringPreferencesKey("stream_audio_type")
        private val PREF_QUALITY = stringPreferencesKey("stream_quality")
        private val PREF_AUTO_PLAY = booleanPreferencesKey("stream_auto_play")
        private val PREF_AUTO_NEXT = booleanPreferencesKey("stream_auto_next")
        private val PREF_AUTO_SKIP_INTRO = booleanPreferencesKey("stream_auto_skip_intro")
        private val PREF_AUTO_SKIP_OUTRO = booleanPreferencesKey("stream_auto_skip_outro")
        private val PREF_API_BASE_URL = stringPreferencesKey("stream_api_base_url")

        const val DEFAULT_PROVIDER = "kiwi"
        const val DEFAULT_API_URL = "https://miruro-api.vercel.app"

        val QUALITY_OPTIONS = listOf("1080p", "720p", "480p", "360p")
        val KNOWN_PROVIDERS = listOf("kiwi", "hop", "ally", "bonk", "moo", "bee", "ANIMEDUNYA")
    }
}
