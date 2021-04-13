package com.gamatechno.awor.utils

import android.os.Build
import android.os.Process
import java.io.*
import java.util.*

object DeviceCheckUtil {
    init {
        System.loadLibrary("keys")
    }

    private external fun isMagiskPresentNative(): Boolean
    private val blackListedMountPaths = arrayOf(
        "/sbin/.magisk/",
        "/sbin/.core/mirror",
        "/sbin/.core/img",
        "/sbin/.core/db-0/magisk.db"
    )

    fun isEmulator(): Boolean {
        var result = (Build.FINGERPRINT.startsWith("generic")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.toLowerCase(Locale.ROOT).contains("droid4x")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HARDWARE == "goldfish" || Build.HARDWARE == "vbox86" || Build.PRODUCT == "sdk" || Build.PRODUCT == "google_sdk" || Build.PRODUCT == "sdk_x86" || Build.PRODUCT == "vbox86p" || Build.BOARD.toLowerCase()
            .contains("nox")
                || Build.BOOTLOADER.toLowerCase(Locale.ROOT).contains("nox")
                || Build.HARDWARE.toLowerCase(Locale.ROOT).contains("nox")
                || Build.PRODUCT.toLowerCase(Locale.ROOT).contains("nox"))
        if (result) return true
        result = result or (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == Build.PRODUCT)
        if (result) return true
        result = !(Build.getRadioVersion()!=null && Build.getRadioVersion().isNotEmpty())
        return result
    }

    fun isRooted(): Boolean {
        var found = false
        if (!found) {
            val places = arrayOf(
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/",
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/sbin/su/",
                "/system/bin/su",
                "/system/bin/su/",
                "/system/xbin/su",
                "/system/xbin/su/",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
                "/su/bin/su",
                "/su/",
                "/data/local/xbin/",
                "/system/bin/.ext/",
                "/system/bin/failsafe/",
                "/system/sd/xbin/",
                "/su/xbin/",
                "/su/bin/",
                "/magisk/.core/bin/",
                "/system/usr/we-need-root/",
                "/system/xbin/",
                "/system/su",
                "/system/bin/.ext/.su",
                "/system/usr/we-need-root/su-backup",
                "/system/xbin/mu",
                "/system/su/",
                "/system/bin/.ext/.su/",
                "/system/usr/we-need-root/su-backup/",
                "/system/xbin/mu/"
            )
            for (where in places) {
                if (File(where + "su").exists()) {
                    found = true
                    break
                }
            }
        }
        return found
    }

    fun isMagiskPresent(): Boolean {
        var isMagiskPresent = false
        val pid = Process.myPid()
        val cwd = String.format("/proc/%d/mounts", pid)
        val file = File(cwd)
        try {
            val fis = FileInputStream(file)
            val reader =
                BufferedReader(InputStreamReader(fis))
            var str: String?
            var count = 0
            while (reader.readLine().also { str = it } != null) {
                for (path in blackListedMountPaths) {
                    if (str!!.contains(path)) {
                        count++
                    }
                }
            }
            reader.close()
            fis.close()
            isMagiskPresent = if (count > 1) {
                true
            } else {
                isMagiskPresentNative()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return isMagiskPresent
    }
}