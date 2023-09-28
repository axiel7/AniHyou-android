package com.axiel7.anihyou.ui.screens.thread.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.data.model.thread.ChildComment
import com.axiel7.anihyou.ui.composables.CommentIconButton
import com.axiel7.anihyou.ui.composables.DefaultMarkdownText
import com.axiel7.anihyou.ui.composables.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_VERY_SMALL
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.DateUtils.timestampIntervalSinceNow
import java.time.temporal.ChronoUnit

@Composable
fun ChildCommentView(
    comment: ChildComment,
    navigateToUserDetails: () -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        VerticalDivider(
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.clickable {
                        navigateToUserDetails()
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PersonImage(
                        url = comment.user?.avatar?.medium,
                        modifier = Modifier
                            .size(PERSON_IMAGE_SIZE_VERY_SMALL.dp)
                    )
                    Text(
                        text = comment.user?.name ?: "",
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = comment.createdAt.toLong().timestampIntervalSinceNow()
                        .secondsToLegibleText(maxUnit = ChronoUnit.WEEKS),
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 15.sp
                )
            }
            DefaultMarkdownText(
                markdown = comment.comment ?: "",
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 16.sp,
                navigateToFullscreenImage = navigateToFullscreenImage
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                if (!comment.childComments.isNullOrEmpty()) {
                    CommentIconButton(
                        commentCount = comment.childComments.size,
                        onClick = { /*TODO*/ }
                    )
                }
                FavoriteIconButton(
                    isFavorite = false,
                    favoritesCount = comment.likeCount,
                    onClick = { /*TODO*/ }
                )
            }
        }//:Column
    }//:Row
}

@Preview
@Composable
fun ChildCommentViewPreview() {
    AniHyouTheme {
        Surface {
            ChildCommentView(
                comment = ChildComment.preview,
                navigateToUserDetails = {},
                navigateToFullscreenImage = {}
            )
        }
    }
}