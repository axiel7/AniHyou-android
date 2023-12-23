package com.axiel7.anihyou.ui.common.navigation

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