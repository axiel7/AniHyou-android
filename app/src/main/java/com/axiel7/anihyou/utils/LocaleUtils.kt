package com.axiel7.anihyou.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.os.LocaleListCompat
import com.axiel7.anihyou.R
import org.xmlpull.v1.XmlPullParser
import java.util.Locale

object LocaleUtils {

    /**
     * @author https://github.com/tachiyomiorg/tachiyomi
     */
    fun Context.getAvailableLocales(): Map<String, String> {
        val langs = mutableListOf<Pair<String, String>>()
        val parser = resources.getXml(R.xml.locales_config)
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "locale") {
                for (i in 0..<parser.attributeCount) {
                    if (parser.getAttributeName(i) == "name") {
                        val langTag = parser.getAttributeValue(i)
                        val displayName = getDisplayName(langTag)
                        if (displayName.isNotEmpty()) {
                            langs.add(Pair(langTag, displayName))
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        langs.sortBy { it.second }
        langs.add(0, Pair("", getString(R.string.theme_system)))

        return langs.toMap()
    }

    /**
     * Returns display name of a string language code.
     * @author https://github.com/tachiyomiorg/tachiyomi
     * @param lang empty for system language
     */
    private fun getDisplayName(lang: String?): String {
        if (lang == null) {
            return ""
        }

        val locale = when (lang) {
            "" -> LocaleListCompat.getAdjustedDefault()[0]
            "zh-CN" -> Locale.forLanguageTag("zh-Hans")
            "zh-TW" -> Locale.forLanguageTag("zh-Hant")
            else -> Locale.forLanguageTag(lang)
        }
        return locale!!.getDisplayName(locale).replaceFirstChar { it.uppercase(locale) }
    }

    fun getDefaultLocale() =
        AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag().orEmpty()

    fun getCurrentLanguageTag() = LocaleListCompat.getAdjustedDefault()[0]?.toLanguageTag()

    val LocalIsLanguageEn = staticCompositionLocalOf {
        getCurrentLanguageTag()?.startsWith("en") == true
    }

    fun setDefaultLocale(locale: String) {
        val localeList = if (locale.isEmpty()) LocaleListCompat.getEmptyLocaleList()
        else LocaleListCompat.forLanguageTags(locale)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}