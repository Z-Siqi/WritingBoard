package com.sqz.writingboard.common

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * Basic Checks if the device is an emulator.
 */
fun isEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT.lowercase()
    return when {
        Build.FINGERPRINT.startsWith("generic") -> true
        fingerprint.contains("vbox") -> true
        fingerprint.contains("test-keys") -> true
        Build.MODEL.contains("Emulator") -> true
        Build.MODEL.contains("Android SDK built for x86") -> true
        Build.MANUFACTURER.contains("Genymotion") -> true
        Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") -> true
        Build.PRODUCT.contains("sdk_") && Build.PRODUCT.contains("_x86_64") -> true
        Build.PRODUCT == "google_sdk" -> true
        fingerprint.contains("sdk")
                && fingerprint.contains("x86_64")
                && fingerprint.contains("emu") -> true

        else -> false
    }
}

/**
 * Checks if the device is an emulator.
 */
fun isEmulator(context: Context): Boolean {
    val packageManager = context.packageManager
    val featurePC = packageManager.hasSystemFeature(PackageManager.FEATURE_PC)
    val featureTelephony = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

    return when {
        isEmulator() -> true
        featurePC && !featureTelephony -> true
        //!featureTelephony -> true
        else -> false
    }
}
