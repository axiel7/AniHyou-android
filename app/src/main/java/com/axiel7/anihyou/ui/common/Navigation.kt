package com.axiel7.anihyou.ui.common

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.axiel7.anihyou.type.MediaType

enum class ArgumentType {
    String, Int, Boolean, BooleanOptional;

    fun toNavType() = when (this) {
        String -> NavType.StringType
        Int -> NavType.IntType
        Boolean -> NavType.BoolType
        BooleanOptional -> NavType.StringType // null boolean is not supported in androidx.navigation
    }

    val isOptional
        get() = when (this) {
            BooleanOptional -> true
            else -> false
        }
}

enum class NavArgument(
    val type: ArgumentType,
) {
    MediaType(ArgumentType.String),
    MediaSort(ArgumentType.String),
    Genre(ArgumentType.String),
    Tag(ArgumentType.String),
    OnList(ArgumentType.BooleanOptional),
    Focus(ArgumentType.BooleanOptional),
    UserId(ArgumentType.Int),
    UserName(ArgumentType.String),
    ScoreFormat(ArgumentType.String),
    UnreadCount(ArgumentType.Int),
    MediaId(ArgumentType.Int),
    ChartType(ArgumentType.String),
    Season(ArgumentType.String),
    Year(ArgumentType.Int),
    CharacterId(ArgumentType.Int),
    StaffId(ArgumentType.Int),
    ReviewId(ArgumentType.Int),
    ThreadId(ArgumentType.Int),
    StudioId(ArgumentType.Int),
    Url(ArgumentType.String),
    ActivityId(ArgumentType.Int),
    Text(ArgumentType.String),
    ReplyId(ArgumentType.Int),
    CommentId(ArgumentType.Int),
    ParentCommentId(ArgumentType.Int),
    ;
}

data class DestArgument(
    val argument: NavArgument,
    val isNullable: Boolean = false,
    val defaultValue: Any? = null,
) {
    fun toNamedNavArgument() = navArgument(argument.name) {
        type = argument.type.toNavType()
        nullable = isNullable || argument.type.isOptional
        if (this@DestArgument.defaultValue != null) {
            defaultValue = this@DestArgument.defaultValue
        }
    }

    companion object {
        fun NavBackStackEntry.getStringArg(destArgument: DestArgument?) =
            if (destArgument?.argument?.type == ArgumentType.String)
                arguments?.getString(
                    destArgument.argument.name,
                    destArgument.defaultValue as? String?
                )
            else null

        fun NavBackStackEntry.getIntArg(destArgument: DestArgument?) =
            if (destArgument?.argument?.type == ArgumentType.Int)
                if (destArgument.defaultValue is Int)
                    arguments?.getInt(destArgument.argument.name, destArgument.defaultValue)
                else arguments?.getInt(destArgument.argument.name)
            else null

        fun NavBackStackEntry.getBoolean(destArgument: DestArgument?) =
            if (destArgument?.argument?.type == ArgumentType.Boolean) {
                if (destArgument.defaultValue is Boolean)
                    arguments?.getBoolean(destArgument.argument.name, destArgument.defaultValue)
                else arguments?.getBoolean(destArgument.argument.name)
            } else if (destArgument?.argument?.type == ArgumentType.BooleanOptional) {
                if (destArgument.defaultValue is Boolean) {
                    arguments?.getString(
                        destArgument.argument.name,
                        destArgument.defaultValue.toString()
                    )?.toBooleanStrictOrNull()
                } else arguments?.getString(destArgument.argument.name)?.toBooleanStrictOrNull()
            } else null
    }
}

enum class NavDestination(
    private val arguments: List<DestArgument> = emptyList()
) {
    HomeTab,

    AnimeTab(
        arguments = listOf(
            DestArgument(
                argument = NavArgument.MediaType,
                defaultValue = MediaType.ANIME.rawValue
            )
        )
    ),

    MangaTab(
        arguments = listOf(
            DestArgument(
                argument = NavArgument.MediaType,
                defaultValue = MediaType.MANGA.rawValue
            )
        )
    ),

    ProfileTab,

    ExploreTab,

    Search(
        arguments = listOf(
            DestArgument(
                argument = NavArgument.MediaType,
                isNullable = true,
            ),
            DestArgument(
                argument = NavArgument.MediaSort,
                isNullable = true,
            ),
            DestArgument(
                argument = NavArgument.Genre,
                isNullable = true,
            ),
            DestArgument(
                argument = NavArgument.Tag,
                isNullable = true,
            ),
            DestArgument(
                argument = NavArgument.OnList,
                isNullable = true,
            ),
            DestArgument(
                argument = NavArgument.Focus,
                isNullable = true,
            )
        )
    ),

    UserMediaList(
        arguments = listOf(
            DestArgument(argument = NavArgument.MediaType),
            DestArgument(argument = NavArgument.UserId),
            DestArgument(argument = NavArgument.ScoreFormat),
        )
    ),

    Notifications(
        arguments = listOf(
            DestArgument(argument = NavArgument.UnreadCount)
        )
    ),

    MediaDetails(
        arguments = listOf(
            DestArgument(argument = NavArgument.MediaId)
        )
    ),

    MediaChart(
        arguments = listOf(
            DestArgument(argument = NavArgument.ChartType)
        )
    ),

    SeasonAnime(
        arguments = listOf(
            DestArgument(argument = NavArgument.Season),
            DestArgument(argument = NavArgument.Year)
        )
    ),

    Calendar,

    UserDetails(
        arguments = listOf(
            DestArgument(argument = NavArgument.UserId),
            DestArgument(
                argument = NavArgument.UserName,
                isNullable = true
            )
        )
    ),

    CharacterDetails(
        arguments = listOf(
            DestArgument(argument = NavArgument.CharacterId)
        )
    ),

    StaffDetails(
        arguments = listOf(
            DestArgument(argument = NavArgument.StaffId)
        )
    ),

    ReviewDetails(
        arguments = listOf(
            DestArgument(argument = NavArgument.ReviewId)
        )
    ),

    ThreadDetails(
        arguments = listOf(
            DestArgument(argument = NavArgument.ThreadId)
        )
    ),

    StudioDetails(
        arguments = listOf(
            DestArgument(argument = NavArgument.StudioId)
        )
    ),

    Settings,

    ListStyleSettings,

    Translations,

    FullscreenImage(
        arguments = listOf(
            DestArgument(argument = NavArgument.Url)
        )
    ),

    ActivityDetails(
        arguments = listOf(
            DestArgument(argument = NavArgument.ActivityId)
        )
    ),

    PublishActivity(
        arguments = listOf(
            DestArgument(argument = NavArgument.ActivityId),
            DestArgument(
                argument = NavArgument.Text,
                isNullable = true
            )
        )
    ),

    PublishActivityReply(
        arguments = listOf(
            DestArgument(argument = NavArgument.ActivityId),
            DestArgument(argument = NavArgument.ReplyId),
            DestArgument(
                argument = NavArgument.Text,
                isNullable = true
            )
        )
    ),

    PublishThreadComment(
        arguments = listOf(
            DestArgument(argument = NavArgument.ThreadId),
            DestArgument(argument = NavArgument.CommentId),
            DestArgument(
                argument = NavArgument.Text,
                isNullable = true
            )
        )
    ),

    PublishCommentReply(
        arguments = listOf(
            DestArgument(argument = NavArgument.ParentCommentId),
            DestArgument(argument = NavArgument.CommentId),
            DestArgument(
                argument = NavArgument.Text,
                isNullable = true
            )
        )
    ),
    ;

    fun findDestArgument(argument: NavArgument) = arguments.find { it.argument == argument }

    fun route() = if (arguments.isEmpty()) name else {
        name + arguments
            .sortedBy { it.isNullable }
            .joinToString(separator = "") { arg ->
                if (arg.isNullable) "?${arg.argument.name}={${arg.argument.name}}"
                else "/{${arg.argument.name}}"
            }
    }

    val namedNavArguments get() = arguments.map { it.toNamedNavArgument() }

    fun putArguments(arguments: Map<NavArgument, String?>): String {
        var routeWithArguments = route()
        arguments.forEach { (arg, value) ->
            if (value != null)
                routeWithArguments = routeWithArguments.replace("{${arg.name}}", value)
        }
        return routeWithArguments
    }
}