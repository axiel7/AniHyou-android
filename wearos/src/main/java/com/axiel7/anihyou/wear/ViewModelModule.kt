package com.axiel7.anihyou.wear

import com.axiel7.anihyou.wear.ui.screens.login.LoginViewModel
import com.axiel7.anihyou.wear.ui.screens.main.MainViewModel
import com.axiel7.anihyou.wear.ui.screens.usermedialist.UserMediaListViewModel
import com.axiel7.anihyou.wear.ui.screens.usermedialist.edit.EditMediaViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::UserMediaListViewModel)
    viewModelOf(::EditMediaViewModel)
}