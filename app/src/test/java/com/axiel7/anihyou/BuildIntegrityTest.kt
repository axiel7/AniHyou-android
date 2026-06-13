package com.axiel7.anihyou

import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.common.utils.NumberUtils.isGreaterThanZero
import com.axiel7.anihyou.core.common.utils.NumberUtils.toStringOrUnknown
import com.axiel7.anihyou.core.common.utils.NumberUtils.toDoubleLocaleInvariant
import com.axiel7.anihyou.core.common.utils.NumberUtils.toInt
import com.axiel7.anihyou.core.common.utils.NumberUtils.isNullOrZero
import com.axiel7.anihyou.core.common.utils.StringUtils.htmlStripped
import com.axiel7.anihyou.core.common.utils.StringUtils.orUnknown
import com.axiel7.anihyou.core.common.utils.StringUtils.slugify
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * BuildIntegrityTest — guards the known-good state at commit 29dc040.
 *
 * These tests cover pure-JVM utility functions that have zero Android
 * framework dependencies so they run with plain `./gradlew test` in CI
 * without an emulator.
 *
 * Rule: every GREEN build must keep all of these passing.
 * When new features land on master, add feature-specific tests here or in
 * their own *Test.kt file — never delete existing assertions.
 */
class BuildIntegrityTest {

    // ── StringUtils ──────────────────────────────────────────────────────────

    @Test
    fun `orUnknown returns value when non-null`() {
        assertEquals("hello", "hello".orUnknown())
    }

    @Test
    fun `orUnknown returns UNKNOWN_CHAR for null`() {
        val s: String? = null
        assertEquals(UNKNOWN_CHAR, s.orUnknown())
    }

    @Test
    fun `htmlStripped removes tags`() {
        val raw = "<b>bold</b> and <i>italic</i>"
        assertEquals("bold and italic", raw.htmlStripped())
    }

    @Test
    fun `htmlStripped is no-op on plain text`() {
        assertEquals("plain text", "plain text".htmlStripped())
    }

    @Test
    fun `slugify converts spaces to hyphens`() {
        // Implementation preserves case — no lowercase step
        assertEquals("Re-Zero", "Re Zero".slugify())
    }

    @Test
    fun `slugify strips special characters`() {
        // Strips ! but preserves case
        assertEquals("Attack-on-Titan", "Attack on Titan!".slugify())
    }

    @Test
    fun `slugify output contains only alphanumeric and hyphens`() {
        // Case is preserved; only [a-zA-Z0-9-] chars remain
        val result = "Shingeki no Kyojin".slugify()
        assertTrue(result.matches(Regex("[a-zA-Z0-9\\-]+")))
    }

    // ── NumberUtils ──────────────────────────────────────────────────────────

    @Test
    fun `Double format with 2 decimals`() {
        val result = 8.567.format(2)
        assertNotNull(result)
        // formatted value should start with "8"
        assertTrue(result!!.startsWith("8"))
    }

    @Test
    fun `Double format with 0 decimals`() {
        val result = 8.567.format(0)
        assertEquals("9", result)   // DecimalFormat rounds half-up
    }

    @Test
    fun `Int toStringOrUnknown returns string for non-null`() {
        assertEquals("42", 42.toStringOrUnknown())
    }

    @Test
    fun `null Int toStringOrUnknown returns UNKNOWN_CHAR`() {
        val n: Int? = null
        assertEquals(UNKNOWN_CHAR, n.toStringOrUnknown())
    }

    @Test
    fun `Int isGreaterThanZero true for positive`() {
        assertTrue(5.isGreaterThanZero())
    }

    @Test
    fun `Int isGreaterThanZero false for zero`() {
        assertFalse(0.isGreaterThanZero())
    }

    @Test
    fun `null Int isGreaterThanZero is false`() {
        val n: Int? = null
        assertFalse(n.isGreaterThanZero())
    }

    @Test
    fun `Double isNullOrZero true for null`() {
        val d: Double? = null
        assertTrue(d.isNullOrZero())
    }

    @Test
    fun `Double isNullOrZero true for zero`() {
        assertTrue(0.0.isNullOrZero())
    }

    @Test
    fun `Double isNullOrZero false for non-zero`() {
        assertFalse(8.5.isNullOrZero())
    }

    @Test
    fun `Boolean toInt true is 1`() {
        assertEquals(1, true.toInt())
    }

    @Test
    fun `Boolean toInt false is 0`() {
        assertEquals(0, false.toInt())
    }

    @Test
    fun `null Boolean toInt is 0`() {
        val b: Boolean? = null
        assertEquals(0, b.toInt())
    }

    @Test
    fun `toDoubleLocaleInvariant parses valid number`() {
        val result = "8.5".toDoubleLocaleInvariant()
        assertNotNull(result)
        assertEquals(8.5, result!!, 0.001)
    }

    @Test
    fun `toDoubleLocaleInvariant returns null for garbage`() {
        assertNull("not_a_number".toDoubleLocaleInvariant())
    }

    // ── DateUtils (pure JVM subset) ──────────────────────────────────────────

    @Test
    fun `minutesToDays converts correctly`() {
        with(com.axiel7.anihyou.core.common.utils.DateUtils) {
            assertEquals(1, 1440.minutesToDays())
            assertEquals(2, 2880.minutesToDays())
            assertEquals(0, 60.minutesToDays())
        }
    }

    @Test
    fun `LocalDate toEpochMillis is positive`() {
        with(com.axiel7.anihyou.core.common.utils.DateUtils) {
            val millis = LocalDate.of(2024, 1, 1).toEpochMillis()
            assertTrue(millis > 0)
        }
    }

    @Test
    fun `LocalDate toFuzzyDateInt produces 8-digit int`() {
        with(com.axiel7.anihyou.core.common.utils.DateUtils) {
            val fuzzy = LocalDate.of(2024, 6, 15).toFuzzyDateInt()
            assertEquals(20240615, fuzzy)
        }
    }
}
