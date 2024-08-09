package com.axiel7.anihyou

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.DateUtils.timestampIntervalSinceNow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.temporal.ChronoUnit

@RunWith(AndroidJUnit4::class)
class DateTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            MaterialTheme {
                ParseCreatedAtToDate()
            }
        }
    }

    @Composable
    fun ParseCreatedAtToDate() {
        val createdAt = 1607910968
        Text(
            text = createdAt.toLong().timestampIntervalSinceNow()
                .secondsToLegibleText(
                    maxUnit = ChronoUnit.WEEKS,
                    isFutureDate = false
                ),
            modifier = Modifier.testTag("date")
        )
    }

    @Test
    fun is_date_parsed_correctly() {
        composeTestRule.onNodeWithTag("date").assertTextEquals("Dec 14, 2020")
    }
}
