package com.axiel7.anihyou.ui.common.navigation

import com.axiel7.anihyou.type.MediaType

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
            // remove remaining optional arguments
            .replace(Regex("\\?\\D*=\\{\\D*\\}"), "")
    }
}