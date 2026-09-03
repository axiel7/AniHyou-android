package com.axiel7.anihyou.core.common.utils

import android.os.Build
import androidx.core.performance.DefaultDevicePerformance

object DeviceUtils {
    fun isDevicePowerfulEnough(): Boolean {
        val perfClass = DefaultDevicePerformance().mediaPerformanceClass

        if (perfClass >= Build.VERSION_CODES.S) {
            android.util.Log.d("PerfCheck", "Passed via Performance Class: $perfClass")
            return true
        }

        val cores = Runtime.getRuntime().availableProcessors()
        var totalRamGb = 0.0

        try {
            val reader = java.io.RandomAccessFile("/proc/meminfo", "r")
            val memInfo = reader.readLine()

            if (memInfo != null && memInfo.startsWith("MemTotal")) {
                val ramKb = memInfo.replace(Regex("\\D+"), "").toLongOrNull() ?: 0L
                totalRamGb = ramKb / (1024.0 * 1024.0)
            }
            reader.close()
        } catch (e: Exception) {
            android.util.Log.e("PerfCheck", "Failed to read RAM for fallback", e)
        }

        android.util.Log.d(
            "PerfCheck",
            "PerfClass was $perfClass. Fallback -> Cores: $cores | RAM: ${String.format("%.2f", totalRamGb)} GB"
        )

        return totalRamGb >= 5.5 && cores >= 6
    }
}