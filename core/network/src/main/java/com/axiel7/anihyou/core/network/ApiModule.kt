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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val apiModule = module {
    singleOf(::ActivityApi)
    singleOf(::CharacterApi)
    singleOf(::FavoriteApi)
    singleOf(::LikeApi)
    singleOf(::MalApi)
    singleOf(::MediaApi)
    singleOf(::MediaListApi)
    singleOf(::NotificationsApi)
    singleOf(::ReviewApi)
    singleOf(::StaffApi)
    singleOf(::StudioApi)
    singleOf(::ThreadApi)
    singleOf(::UserApi)
}