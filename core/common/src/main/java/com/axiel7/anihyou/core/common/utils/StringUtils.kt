package com.axiel7.anihyou.core.common.utils

import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import java.text.Normalizer
import kotlin.math.abs

object StringUtils {
    fun String?.orUnknown() = this ?: UNKNOWN_CHAR

    fun String.htmlStripped() = replace(Regex("<[^>]+>"), "")

    fun String.slugify() = Normalizer
        .normalize(this, Normalizer.Form.NFD)
        .replace(Regex("[^\\p{ASCII}]"), "")
        .replace(Regex("[^a-zA-Z0-9\\s]+"), "")
        .trim()
        .replace(Regex("\\s+"), "-")


    private val tokenRegex = "[\\s_\\-:,./]+".toRegex()
    val whiteSpaceRegex = "\\s+".toRegex()

    fun String?.fuzzyScore(query: String, queryTokens: List<String>): Int {
        if (this.isNullOrBlank() || queryTokens.isEmpty()) return 0
        val text = this.lowercase()
        val q = query.trim().lowercase()

        if (text == q) return 1000
        if (text.startsWith(q)) return 900

        val words = text.split(tokenRegex).filter { it.isNotEmpty() }
        val acronyms by lazy { text.extractAcronyms() }

        var totalScore = 0

        val allTokensMatch = queryTokens.all { token ->
            var bestWordScore = 0

            if (acronyms.contains(token)) {
                bestWordScore = maxOf(bestWordScore, 800)
            }

            for (word in words) {
                if (word == token) {
                    bestWordScore = maxOf(bestWordScore, 700)
                } else if (word.startsWith(token)) {
                    bestWordScore = maxOf(bestWordScore, 600 + token.length)
                } else {
                    val maxDist = when {
                        token.length <= 3 -> 0
                        token.length <= 7 -> 1
                        else -> 2
                    }

                    if (maxDist > 0) {
                        // Fuzzy prefix score
                        if (word.length > token.length && token.first() == word.first()) {
                            val minLen = maxOf(1, token.length - maxDist)
                            val maxLen = minOf(word.length, token.length + maxDist)
                            var bestDist = maxDist + 1

                            for (len in minLen..maxLen) {
                                val dist =
                                    damerauLevenshtein(token, word.substring(0, len), maxDist)
                                if (dist < bestDist) bestDist = dist
                            }

                            if (bestDist <= maxDist) {
                                bestWordScore = maxOf(bestWordScore, 500 - (bestDist * 50))
                            }
                        }

                        // Full word typo score
                        if (abs(token.length - word.length) <= maxDist) {
                            val dist = damerauLevenshtein(token, word, maxDist)
                            if (dist <= maxDist) {
                                bestWordScore = maxOf(bestWordScore, 400 - (dist * 50))
                            }
                        }
                    }
                }
            }

            if (bestWordScore > 0) {
                totalScore += bestWordScore
                true
            } else {
                false
            }
        }

        return if (allTokensMatch) totalScore / queryTokens.size else 0
    }

    fun String?.fuzzyMatch(query: String, queryTokens: List<String>): Boolean {
        if (this.isNullOrBlank() || queryTokens.isEmpty()) return false
        val text = this.lowercase()
        val q = query.trim().lowercase()

        if (text.contains(q)) return true

        // Token search
        val words = text.split(tokenRegex).filter { it.isNotEmpty() }
        val acronyms by lazy { text.extractAcronyms() }

        return queryTokens.all { token ->
            val matchesWord = words.any { word ->
                if (word == token || word.startsWith(token)) return@any true

                val maxDist = when {
                    token.length <= 3 -> 0
                    token.length <= 7 -> 1
                    else -> 2
                }
                if (maxDist == 0) return@any false

                // fuzzy prefix
                if (word.length > token.length && token.first() == word.first()) {
                    val minLen = maxOf(1, token.length - maxDist)
                    val maxLen = minOf(word.length, token.length + maxDist)
                    for (len in minLen..maxLen) {
                        if (damerauLevenshtein(token, word.substring(0, len), maxDist) <= maxDist) {
                            return@any true
                        }
                    }
                }

                // Full word typo check
                if (abs(token.length - word.length) <= maxDist) {
                    if (damerauLevenshtein(token, word, maxDist) <= maxDist) return@any true
                }
                false
            }
            matchesWord || acronyms.contains(token)
        }
    }

    private fun String.extractAcronyms(): List<String> {
        val result = mutableListOf<String>()
        val currentAcronym = StringBuilder()
        var isNewWord = true

        for (i in this.indices) {
            val c = this[i]
            if (c == ':' || c == '-' || c == '~') {
                if (currentAcronym.isNotEmpty()) {
                    result.add(currentAcronym.toString())
                    currentAcronym.clear()
                }
                isNewWord = true
            } else if (c.isWhitespace() || c == '_' || c == '.' || c == ',' || c == '/') {
                isNewWord = true
            } else if (isNewWord) {
                currentAcronym.append(c)
                isNewWord = false
            }
        }

        if (currentAcronym.isNotEmpty()) {
            result.add(currentAcronym.toString())
        }
        return result
    }

    private fun isSimilarWord(token: String, word: String): Boolean {
        val maxDist = when {
            token.length <= 3 -> 0
            token.length <= 7 -> 1
            else -> 2
        }

        if (maxDist == 0 || abs(token.length - word.length) > maxDist) return false
        return levenshteinDistance(token, word, maxDist) <= maxDist
    }

    private fun damerauLevenshtein(s1: String, s2: String, maxDist: Int): Int {
        if (abs(s1.length - s2.length) > maxDist) return maxDist + 1

        var v0 = IntArray(s2.length + 1) { it }
        var v1 = IntArray(s2.length + 1)
        var v2 = IntArray(s2.length + 1)

        for (i in 1..s1.length) {
            v1[0] = i
            var minRowCost = i
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                var current = minOf(
                    v0[j] + 1,
                    v1[j - 1] + 1,
                    v0[j - 1] + cost
                )

                if (i > 1 && j > 1 && s1[i - 1] == s2[j - 2] && s1[i - 2] == s2[j - 1]) {
                    current = minOf(current, v2[j - 2] + cost)
                }

                v1[j] = current
                if (current < minRowCost) minRowCost = current
            }
            if (minRowCost > maxDist) return maxDist + 1

            val temp = v2
            v2 = v0
            v0 = v1
            v1 = temp
        }
        return v0[s2.length]
    }

    private fun levenshteinDistance(s1: String, s2: String, maxDist: Int): Int {
        val costs = IntArray(s2.length + 1) { it }

        for (i in 1..s1.length) {
            costs[0] = i
            var nw = i - 1
            var minRowCost = costs[0]

            for (j in 1..s2.length) {
                val cj =
                    (1 + costs[j].coerceAtMost(costs[j - 1])).coerceAtMost(if (s1[i - 1] == s2[j - 1]) nw else nw + 1)
                nw = costs[j]
                costs[j] = cj

                if (cj < minRowCost) {
                    minRowCost = cj
                }
            }

            if (minRowCost > maxDist) {
                return maxDist + 1
            }
        }
        return costs[s2.length]
    }
}