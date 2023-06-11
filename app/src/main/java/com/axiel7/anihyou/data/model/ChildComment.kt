package com.axiel7.anihyou.data.model

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import com.apollographql.apollo3.api.list
import com.apollographql.apollo3.api.nullable
import com.apollographql.apollo3.api.obj
import com.axiel7.anihyou.ChildCommentsQuery
import com.axiel7.anihyou.adapter.ChildCommentsQuery_ResponseAdapter

typealias ChildComments = List<ChildCommentsQuery.ThreadComment?>

data class ChildComment(
    val id: Int,
    val comment: String?,
    val likeCount: Int,
    val createdAt: Int,
    val user: User?,
    var childComments: List<ChildComment>?,
) {
    companion object {
        data class User(
            val id: Int,
            val name: String,
            val avatar: Avatar?
        )

        data class Avatar(
            val medium: String?
        )
    }
}

val jsonChildCommentAdapter = object : Adapter<List<ChildCommentsQuery.ThreadComment?>> {
    override fun fromJson(
        reader: JsonReader,
        customScalarAdapters: CustomScalarAdapters
    ): List<ChildCommentsQuery.ThreadComment?> {
        var childComments: List<ChildCommentsQuery.ThreadComment?>? = null

        while(true) {
            when (reader.selectName(ChildCommentsQuery_ResponseAdapter.Data.RESPONSE_NAMES)) {
                0 -> childComments = ChildCommentsQuery_ResponseAdapter.ThreadComment.obj().nullable().list().nullable().fromJson(reader,
                    customScalarAdapters)
                else -> break
            }
        }

        /*while (reader.hasNext()) {
            var id: Int? = null
            var comment: String? = null
            var likeCount: Int? = null
            var createdAt: Int? = null
            var user: ChildComment.Companion.User? = null
            var childCommentComments: List<ChildComment>? = null
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "id" -> id = reader.nextInt()
                    "comment" -> comment = reader.nextString()
                    "likeCount" -> likeCount = reader.nextInt()
                    "createdAt" -> createdAt = reader.nextInt()
                    "user" -> {
                        var userId: Int? = null
                        var userName: String? = null
                        var avatar: ChildComment.Companion.Avatar? = null
                        reader.beginObject()
                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                "id" -> userId = reader.nextInt()
                                "name" -> userName = reader.nextString()
                                "avatar" -> {
                                    var medium: String? = null
                                    reader.beginObject()
                                    while (reader.hasNext()) {
                                        when (reader.nextName()) {
                                            "medium" -> medium = reader.nextString()
                                            "__typename" -> reader.readTypename()
                                        }
                                    }//Avatar
                                    reader.endObject()
                                    avatar = ChildComment.Companion.Avatar(medium = medium)
                                }
                                "__typename" -> {
                                    reader.readTypename()
                                }
                            }
                        }//User
                        reader.endObject()
                        user = ChildComment.Companion.User(
                            id = userId!!,
                            name = userName!!,
                            avatar = avatar
                        )
                    }
                    "childComments" -> {
                        childCommentComments = fromJson(reader, customScalarAdapters)
                    }
                    "__typename" -> {
                        reader.readTypename()
                    }
                }
            }//ChildComment
            reader.endObject()
            childComments.add(
                ChildComment(
                    id = id!!,
                    comment = comment,
                    likeCount = likeCount!!,
                    createdAt = createdAt!!,
                    user = user!!,
                    childComments = childCommentComments
                )
            )
        }*/
        reader.endArray()
        return childComments ?: emptyList()
    }

    override fun toJson(
        writer: JsonWriter,
        customScalarAdapters: CustomScalarAdapters,
        value: List<ChildCommentsQuery.ThreadComment?>
    ) {
        TODO("Not yet implemented")
    }
}
