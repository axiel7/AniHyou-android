package com.axiel7.anihyou

import com.axiel7.anihyou.feature.activitydetails.ActivityDetailsViewModel
import com.axiel7.anihyou.feature.activitydetails.publish.PublishActivityViewModel
import com.axiel7.anihyou.feature.calendar.CalendarHostViewModel
import com.axiel7.anihyou.feature.calendar.CalendarViewModel
import com.axiel7.anihyou.feature.characterdetails.CharacterDetailsViewModel
import com.axiel7.anihyou.feature.editmedia.EditMediaViewModel
import com.axiel7.anihyou.feature.explore.charts.MediaChartViewModel
import com.axiel7.anihyou.feature.explore.search.SearchViewModel
import com.axiel7.anihyou.feature.explore.search.genretag.GenresTagsViewModel
import com.axiel7.anihyou.feature.explore.season.SeasonAnimeViewModel
import com.axiel7.anihyou.feature.home.HomeViewModel
import com.axiel7.anihyou.feature.home.activity.ActivityFeedViewModel
import com.axiel7.anihyou.feature.home.current.CurrentViewModel
import com.axiel7.anihyou.feature.home.discover.DiscoverViewModel
import com.axiel7.anihyou.feature.mediadetails.MediaDetailsViewModel
import com.axiel7.anihyou.feature.mediadetails.activity.MediaActivityViewModel
import com.axiel7.anihyou.feature.notifications.NotificationsViewModel
import com.axiel7.anihyou.feature.profile.ProfileViewModel
import com.axiel7.anihyou.feature.profile.favorites.UserFavoritesViewModel
import com.axiel7.anihyou.feature.profile.social.UserSocialViewModel
import com.axiel7.anihyou.feature.profile.stats.UserStatsViewModel
import com.axiel7.anihyou.feature.reviewdetails.ReviewDetailsViewModel
import com.axiel7.anihyou.feature.settings.SettingsViewModel
import com.axiel7.anihyou.feature.settings.customlists.CustomListsViewModel
import com.axiel7.anihyou.feature.settings.liststyle.ListStyleSettingsViewModel
import com.axiel7.anihyou.feature.staffdetails.StaffDetailsViewModel
import com.axiel7.anihyou.feature.studiodetails.StudioDetailsViewModel
import com.axiel7.anihyou.feature.thread.ThreadDetailsViewModel
import com.axiel7.anihyou.feature.thread.publish.PublishCommentViewModel
import com.axiel7.anihyou.feature.usermedialist.UserMediaListViewModel
import com.axiel7.anihyou.ui.screens.main.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::ActivityDetailsViewModel)
    viewModelOf(::PublishActivityViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::CalendarHostViewModel)
    viewModelOf(::CharacterDetailsViewModel)
    viewModelOf(::EditMediaViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::GenresTagsViewModel)
    viewModelOf(::MediaChartViewModel)
    viewModelOf(::SeasonAnimeViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ActivityFeedViewModel)
    viewModelOf(::CurrentViewModel)
    viewModelOf(::DiscoverViewModel)
    viewModelOf(::MediaDetailsViewModel)
    viewModelOf(::MediaActivityViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::UserFavoritesViewModel)
    viewModelOf(::UserSocialViewModel)
    viewModelOf(::UserStatsViewModel)
    viewModelOf(::ReviewDetailsViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::CustomListsViewModel)
    viewModelOf(::ListStyleSettingsViewModel)
    viewModelOf(::StaffDetailsViewModel)
    viewModelOf(::StudioDetailsViewModel)
    viewModelOf(::ThreadDetailsViewModel)
    viewModelOf(::PublishCommentViewModel)
    viewModelOf(::UserMediaListViewModel)
}