package com.axiel7.anihyou.core.network

import com.axiel7.anihyou.core.network.api.ActivityApi
import com.axiel7.anihyou.core.network.api.CharacterApi
import com.axiel7.anihyou.core.network.api.FavoriteApi
import com.axiel7.anihyou.core.network.api.LikeApi
import com.axiel7.anihyou.core.network.api.MalApi
import com.axiel7.anihyou.core.network.api.MediaApi
import com.axiel7.anihyou.core.network.api.MediaListApi
import com.axiel7.anihyou.core.network.api.NotificationsApi
import com.axiel7.anihyou.core.network.api.ReviewApi
import com.axiel7.anihyou.core.network.api.StaffApi
import com.axiel7.anihyou.core.network.api.StudioApi
import com.axiel7.anihyou.core.network.api.ThreadApi
import com.axiel7.anihyou.core.network.api.UserApi
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val apiModule = module {
    single<ActivityApi>()
    single<CharacterApi>()
    single<FavoriteApi>()
    single<LikeApi>()
    single<MalApi>()
    single<MediaApi>()
    single<MediaListApi>()
    single<NotificationsApi>()
    single<ReviewApi>()
    single<StaffApi>()
    single<StudioApi>()
    single<ThreadApi>()
    single<UserApi>()
}