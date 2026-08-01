package com.axiel7.anihyou.core.domain

import com.axiel7.anihyou.core.domain.repository.ActivityRepository
import com.axiel7.anihyou.core.domain.repository.CharacterRepository
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.domain.repository.LikeRepository
import com.axiel7.anihyou.core.domain.repository.ListPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.LoginRepository
import com.axiel7.anihyou.core.domain.repository.MediaListRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.domain.repository.NotificationRepository
import com.axiel7.anihyou.core.domain.repository.ReviewRepository
import com.axiel7.anihyou.core.domain.repository.SearchRepository
import com.axiel7.anihyou.core.domain.repository.StaffRepository
import com.axiel7.anihyou.core.domain.repository.StudioRepository
import com.axiel7.anihyou.core.domain.repository.ThreadRepository
import com.axiel7.anihyou.core.domain.repository.UserRepository
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val repositoryModule = module {
    single<ActivityRepository>()
    single<CharacterRepository>()
    single<DefaultPreferencesRepository>()
    single<FavoriteRepository>()
    single<LikeRepository>()
    single<ListPreferencesRepository>()
    single<LoginRepository>()
    single<MediaListRepository>()
    single<MediaRepository>()
    single<NotificationRepository>()
    single<ReviewRepository>()
    single<SearchRepository>()
    single<StaffRepository>()
    single<StudioRepository>()
    single<ThreadRepository>()
    single<UserRepository>()
}