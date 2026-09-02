package com.atlas.app

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.content.pm.PackageManager

class AppsController(private val context: Context) {

    data class AppInfo(
        val label: String,
        val packageName: String,
        val isSystemApp: Boolean,
        val icon: Drawable
    )

    fun getInstalledApps(): List<AppInfo> {
        val packageManager = context.packageManager

        return packageManager
            .getInstalledApplications(PackageManager.GET_META_DATA)
            .map { applicationInfo ->

                AppInfo(
                    label = packageManager
                        .getApplicationLabel(applicationInfo)
                        .toString(),

                    packageName = applicationInfo.packageName,

                    isSystemApp =
                        (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,

                    icon = applicationInfo.loadIcon(packageManager)
                )
            }
            .sortedBy { it.label.lowercase() }
    }
}
