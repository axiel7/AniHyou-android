package com.axiel7.anihyou.wear.ui.screens.login

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.wear.activity.ConfirmationActivity
import androidx.wear.phone.interactions.PhoneTypeHelper
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.axiel7.anihyou.core.base.ANIHYOU_MARKET_URI
import com.axiel7.anihyou.core.base.ANIHYOU_WEAR_CALLBACK_URL
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.resources.R
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : UiStateViewModel<LoginUiState>(), LoginEvent {

    override val initialState = LoginUiState()

    override fun launchLoginIntent(context: Context) {
        mutableUiState.update { it.setError(null) }
        viewModelScope.launch {
            try {
                if (hasPhoneAppInstalled(context)) {
                    RemoteActivityHelper(context).startRemoteActivity(
                        Intent(Intent.ACTION_VIEW)
                            .addCategory(Intent.CATEGORY_BROWSABLE)
                            .setData(ANIHYOU_WEAR_CALLBACK_URL.toUri()),
                    )
                    context.startActivity(
                        Intent(context, ConfirmationActivity::class.java)
                            .putExtra(
                                ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                ConfirmationActivity.OPEN_ON_PHONE_ANIMATION
                            )
                    )
                } else {
                    if (isAndroidPhone(context)) {
                        // launch Play Store
                        RemoteActivityHelper(context).startRemoteActivity(
                            Intent(Intent.ACTION_VIEW)
                                .addCategory(Intent.CATEGORY_BROWSABLE)
                                .setData(ANIHYOU_MARKET_URI.toUri()),
                        )
                    }
                    mutableUiState.update { it.setError(context.getString(R.string.app_installed_required)) }
                }
            } catch (e: RemoteActivityHelper.RemoteIntentException) {
                mutableUiState.update { it.setError(e.message) }
            }
        }
    }

    private fun isAndroidPhone(context: Context) =
        PhoneTypeHelper.getPhoneDeviceType(context) == PhoneTypeHelper.DEVICE_TYPE_ANDROID

    private suspend fun hasPhoneAppInstalled(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            isAndroidPhone(context) && Tasks.await(
                Wearable.getCapabilityClient(context)
                    .getCapability("verify_remote_phone_app", CapabilityClient.FILTER_REACHABLE)
            ).nodes.any { it.isNearby }
        }
    }
}