package com.axiel7.anihyou.data.model.thread

import com.axiel7.anihyou.ChildCommentsQuery

// This model is necessary because AniList API returns a JSON string for the `childComments` query
// and Apollo converts it to an ArrayList of LinkedHashMap
data class ChildComment(
    val id: Int,
    val comment: String?,
    val likeCount: Int,
    val isLiked: Boolean?,
    val createdAt: Int,
    val user: User?,
    val childComments: List<ChildComment?>?,
) {
    data class User(
        val id: Int,
        val name: String,
        val avatar: Avatar?,
    ) {
        data class Avatar(
            val medium: String?,
        )
    }

    @Suppress("UNCHECKED_CAST")
    companion object {

        private fun LinkedHashMap<String, Any?>.toChildComment(): ChildComment? = try {
            ChildComment(
                id = this["id"] as Int,
                comment = this["comment"] as? String?,
                likeCount = this["likeCount"] as Int,
                isLiked = this["isLiked"] as? Boolean?,
                createdAt = this["createdAt"] as Int,
                user = (this["user"] as LinkedHashMap<String, Any?>).toUser(),
                childComments = (this["childComments"] as ArrayList<*>).map {
                    (it as LinkedHashMap<String, Any?>).toChildComment()
                }
            )
        } catch (e: Exception) {
            null
        }

        private fun LinkedHashMap<String, Any?>.toUser(): User? = try {
            User(
                id = this["id"] as Int,
                name = this["name"] as String,
                avatar = (this["avatar"] as LinkedHashMap<String, Any?>).toAvatar()
            )
        } catch (e: Exception) {
            null
        }

        private fun LinkedHashMap<String, Any?>.toAvatar(): User.Avatar? = try {
            val medium = this["medium"] as? String
            User.Avatar(medium)
        } catch (e: Exception) {
            null
        }

        fun ChildCommentsQuery.ThreadComment.toChildComment() =
            ChildComment(
                id = id,
                comment = comment,
                likeCount = likeCount,
                isLiked = isLiked,
                createdAt = createdAt,
                user = if (user != null)
                    User(
                        id = user.id,
                        name = user.name,
                        avatar = if (user.avatar != null) User.Avatar(
                            medium = user.avatar.medium
                        ) else null
                    )
                else null,
                childComments = (childComments as? ArrayList<*>)?.map {
                    (it as LinkedHashMap<String, Any?>).toChildComment()
                }
            )

        val preview = ChildComment(
            id = 1,
            comment = "This is a comment to your comment",
            likeCount = 1,
            isLiked = true,
            createdAt = 1212370032,
            user = null,
            childComments = listOf(
                ChildComment(
                    id = 2,
                    comment = "This is a comment to your comment comment",
                    likeCount = 1,
                    isLiked = false,
                    createdAt = 1212370032,
                    user = null,
                    childComments = null
                )
            )
        )
    }
}
