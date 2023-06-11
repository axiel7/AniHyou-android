package com.axiel7.anihyou.ui.thread

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ChildCommentsQuery
import com.axiel7.anihyou.data.model.ChildComment
import com.axiel7.anihyou.fragment.BasicComment
import com.axiel7.anihyou.ui.base.BaseViewModel

class CommentDetailsViewModel : BaseViewModel() {

    var comment by mutableStateOf<BasicComment?>(null)
    var childComments = mutableStateListOf<ChildCommentsQuery.ThreadComment>()

    /**
     * ChildComments query is very buggy.
     * If the query name contains the `ThreadComment` string,
     * or if we use a fragment. The `childComments` returns null.
     * So I'm doing some hacky things here
     */
    suspend fun getChildComments(commentId: Int) {
        isLoading = true
        val response = ChildCommentsQuery(
            threadCommentId = Optional.present(commentId)
        ).tryQuery()

        response?.data?.ThreadComment?.getOrNull(0)?.let { threadComment ->
            try {
                threadComment.childComments?.let {
                    childComments.addAll(it.filterNotNull())
                }
            } catch (e: Exception) {
                message = e.message ?: e.toString()
            }
            comment = threadComment.toBasicThreadComment()
        }
        isLoading = false
    }

    private fun ChildCommentsQuery.ThreadComment.toBasicThreadComment() =
        BasicComment(
            id = id,
            comment = comment,
            likeCount = likeCount,
            createdAt = createdAt,
            user = if (user != null) BasicComment.User(
                id = user.id,
                name = user.name,
                avatar = if (user.avatar != null) BasicComment.Avatar(
                    medium = user.avatar.medium
                )
                else null,
                __typename = "User"
            )
            else null
        )
}