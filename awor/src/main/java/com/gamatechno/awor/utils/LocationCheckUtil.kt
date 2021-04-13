package com.gamatechno.awor.utils

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location

object LocationCheckUtil {
    fun isLocationFromMockProvider(location: Location): Boolean {
        return location.isFromMockProvider
    }

    fun checkForAllowMockLocationsApps(context: Context): Boolean {
        var count = 0
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in packages) {
            try {
                val packageInfo = pm.getPackageInfo(
                    applicationInfo.packageName,
                    PackageManager.GET_PERMISSIONS
                )

                // Get Permissions
                val requestedPermissions =
                    packageInfo.requestedPermissions
                if (requestedPermissions != null) {
                    for (i in requestedPermissions.indices) {
                        if ((requestedPermissions[i] == "android.permission.ACCESS_MOCK_LOCATION")
                            && applicationInfo.packageName != context.packageName
                        ) {
                            count++
                        }
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) { }
        }
        return count > 0
    }
}