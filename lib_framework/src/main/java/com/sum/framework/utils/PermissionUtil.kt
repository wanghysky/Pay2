package com.sum.framework.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.FragmentActivity

/**
 *
 * @author why
 * @date 2023/11/16 16:51
 */
object PermissionUtil {

    fun openAppSettings(activity: FragmentActivity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        val uri = Uri.fromParts("package", activity.packageName, null);
        intent.setData(uri);
        activity.startActivity(intent);
    }
}