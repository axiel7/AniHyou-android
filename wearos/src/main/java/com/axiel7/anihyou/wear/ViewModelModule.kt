package com.axiel7.anihyou.wear

import com.axiel7.anihyou.wear.ui.screens.login.LoginViewModel
import com.axiel7.anihyou.wear.ui.screens.main.MainViewModel
import com.axiel7.anihyou.wear.ui.screens.usermedialist.UserMediaListViewModel
import com.axiel7.anihyou.wear.ui.screens.usermedialist.edit.EditMediaViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val viewModelModule = module {
    viewModel<MainViewModel>()
    viewModel<LoginViewModel>()
    viewModel<UserMediaListViewModel>()
    viewModel<EditMediaViewModel>()
}