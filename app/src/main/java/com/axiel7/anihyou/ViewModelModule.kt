package com.axiel7.anihyou

import com.axiel7.anihyou.feature.activitydetails.ActivityDetailsViewModel
import com.axiel7.anihyou.feature.activitydetails.publish.PublishActivityViewModel
import com.axiel7.anihyou.feature.calendar.CalendarHostViewModel
import com.axiel7.anihyou.feature.calendar.CalendarViewModel
import com.axiel7.anihyou.feature.characterdetails.CharacterDetailsViewModel
import com.axiel7.anihyou.feature.editmedia.EditMediaViewModel
import com.axiel7.anihyou.feature.explore.charts.MediaChartViewModel
import com.axiel7.anihyou.feature.explore.discover.DiscoverViewModel
import com.axiel7.anihyou.feature.explore.search.SearchViewModel
import com.axiel7.anihyou.feature.explore.search.genretag.GenresTagsViewModel
import com.axiel7.anihyou.feature.explore.season.SeasonAnimeViewModel
import com.axiel7.anihyou.feature.home.HomeViewModel
import com.axiel7.anihyou.feature.home.activity.ActivityFeedViewModel
import com.axiel7.anihyou.feature.home.current.CurrentViewModel
import com.axiel7.anihyou.feature.mediadetails.MediaDetailsViewModel
import com.axiel7.anihyou.feature.mediadetails.activity.MediaActivityViewModel
import com.axiel7.anihyou.feature.mediadetails.characters.MediaCharactersViewModel
import com.axiel7.anihyou.feature.notifications.NotificationsViewModel
import com.axiel7.anihyou.feature.profile.ProfileViewModel
import com.axiel7.anihyou.feature.profile.favorites.UserFavoritesViewModel
import com.axiel7.anihyou.feature.profile.favorites.reorder.ReorderFavoritesViewModel
import com.axiel7.anihyou.feature.profile.social.UserSocialViewModel
import com.axiel7.anihyou.feature.profile.stats.UserStatsViewModel
import com.axiel7.anihyou.feature.reviewdetails.ReviewDetailsViewModel
import com.axiel7.anihyou.feature.settings.SettingsViewModel
import com.axiel7.anihyou.feature.settings.customlists.CustomListsViewModel
import com.axiel7.anihyou.feature.settings.liststyle.ListStyleSettingsViewModel
import com.axiel7.anihyou.feature.settings.priority_colors.PriorityColorViewModel
import com.axiel7.anihyou.feature.staffdetails.StaffDetailsViewModel
import com.axiel7.anihyou.feature.studiodetails.StudioDetailsViewModel
import com.axiel7.anihyou.feature.thread.ThreadDetailsViewModel
import com.axiel7.anihyou.feature.thread.comment.ThreadCommentViewModel
import com.axiel7.anihyou.feature.thread.publish.PublishCommentViewModel
import com.axiel7.anihyou.feature.usermedialist.UserMediaListViewModel
import com.axiel7.anihyou.ui.screens.main.MainViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val viewModelModule = module {
    viewModel<MainViewModel>()
    viewModel<ActivityDetailsViewModel>()
    viewModel<PublishActivityViewModel>()
    viewModel<CalendarViewModel>()
    viewModel<CalendarHostViewModel>()
    viewModel<CharacterDetailsViewModel>()
    viewModel<EditMediaViewModel>()
    viewModel<SearchViewModel>()
    viewModel<GenresTagsViewModel>()
    viewModel<MediaChartViewModel>()
    viewModel<SeasonAnimeViewModel>()
    viewModel<HomeViewModel>()
    viewModel<ActivityFeedViewModel>()
    viewModel<CurrentViewModel>()
    viewModel<DiscoverViewModel>()
    viewModel<MediaDetailsViewModel>()
    viewModel<MediaActivityViewModel>()
    viewModel<NotificationsViewModel>()
    viewModel<ProfileViewModel>()
    viewModel<UserFavoritesViewModel>()
    viewModel<UserSocialViewModel>()
    viewModel<UserStatsViewModel>()
    viewModel<ReviewDetailsViewModel>()
    viewModel<SettingsViewModel>()
    viewModel<CustomListsViewModel>()
    viewModel<ListStyleSettingsViewModel>()
    viewModel<StaffDetailsViewModel>()
    viewModel<StudioDetailsViewModel>()
    viewModel<ThreadDetailsViewModel>()
    viewModel<ThreadCommentViewModel>()
    viewModel<PublishCommentViewModel>()
    viewModel<UserMediaListViewModel>()
    viewModel<ReorderFavoritesViewModel>()
    viewModel<PriorityColorViewModel>()
    viewModel<MediaCharactersViewModel>()
}