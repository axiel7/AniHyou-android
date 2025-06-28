package com.axiel7.anihyou.core.model.activity

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.network.ActivityDetailsQuery
import com.axiel7.anihyou.core.network.fragment.ActivityReplyFragment
import com.axiel7.anihyou.core.network.fragment.ActivityUser
import com.axiel7.anihyou.core.network.fragment.ListActivityFragment
import com.axiel7.anihyou.core.network.type.ActivityType

@Immutable
data class GenericActivity(
    val id: Int,
    val type: ActivityType,
    val createdAt: Int,
    val text: String?,
    val isLiked: Boolean?,
    val likeCount: Int,
    val likes: List<ActivityUser>?,
    val replyCount: Int?,
    val userId: Int?,
    val username: String?,
    val avatarUrl: String?,
    val mediaId: Int? = null,
    val mediaCoverUrl: String? = null,
    val replies: List<ActivityReplyFragment>?,
    val listActivityFragment: ListActivityFragment? = null,
) {
    fun updateLikeStatus(isLiked: Boolean) = copy(
        isLiked = isLiked,
        likeCount = if (isLiked) likeCount + 1 else likeCount - 1
    )
}

fun ActivityDetailsQuery.OnTextActivity.toGenericActivity() = GenericActivity(
    id = textActivityFragment.id,
    type = ActivityType.TEXT,
    createdAt = textActivityFragment.createdAt,
    text = textActivityFragment.text,
    isLiked = textActivityFragment.isLiked,
    likeCount = textActivityFragment.likeCount,
    likes = textActivityFragment.likes?.mapNotNull { it?.activityUser },
    replyCount = textActivityFragment.replyCount,
    userId = textActivityFragment.user?.id,
    username = textActivityFragment.user?.activityUser?.name,
    avatarUrl = textActivityFragment.user?.activityUser?.avatar?.medium,
    replies = replies?.filterNotNull()?.map { it.activityReplyFragment }
)

fun ActivityDetailsQuery.OnListActivity.toGenericActivity() = GenericActivity(
    id = listActivityFragment.id,
    type = ActivityType.MEDIA_LIST,
    createdAt = listActivityFragment.createdAt,
    text = null,
    isLiked = listActivityFragment.isLiked,
    likeCount = listActivityFragment.likeCount,
    likes = listActivityFragment.likes?.mapNotNull { it?.activityUser },
    replyCount = listActivityFragment.replyCount,
    userId = user?.id,
    username = user?.activityUser?.name,
    avatarUrl = user?.activityUser?.avatar?.medium,
    mediaId = listActivityFragment.media?.id,
    mediaCoverUrl = listActivityFragment.media?.coverImage?.medium,
    replies = replies?.filterNotNull()?.map { it.activityReplyFragment },
    listActivityFragment = listActivityFragment,
)

fun ActivityDetailsQuery.OnMessageActivity.toGenericActivity() = GenericActivity(
    id = messageActivityFragment.id,
    type = ActivityType.MESSAGE,
    createdAt = messageActivityFragment.createdAt,
    text = messageActivityFragment.message,
    isLiked = messageActivityFragment.isLiked,
    likeCount = messageActivityFragment.likeCount,
    likes = messageActivityFragment.likes?.mapNotNull { it?.activityUser },
    replyCount = messageActivityFragment.replyCount,
    userId = messageActivityFragment.messenger?.id,
    username = messageActivityFragment.messenger?.activityUser?.name,
    avatarUrl = messageActivityFragment.messenger?.activityUser?.avatar?.medium,
    replies = replies?.filterNotNull()?.map { it.activityReplyFragment }
)

fun ActivityReplyFragment.toGenericActivity() = GenericActivity(
    id = id,
    type = ActivityType.TEXT,
    createdAt = createdAt,
    text = text,
    isLiked = isLiked,
    likeCount = likeCount,
    likes = likes?.mapNotNull { it?.activityUser },
    replyCount = null,
    userId = user?.id,
    username = user?.activityUser?.name,
    avatarUrl = user?.activityUser?.avatar?.medium,
    replies = null,
)

val exampleActivityUser = ActivityUser(
    name = "HelloMyNameIsLong",
    avatar = ActivityUser.Avatar(medium = "https://picsum.photos/200"),
    __typename = "",
    id = 1,
)