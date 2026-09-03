package com.axiel7.anihyou.core.common.utils

import android.os.Build
import android.util.Log

object DeviceUtils {

    private const val RAM_GB_LIMIT = 5
    private const val CPU_CORES_LIMIT = 6

    fun isDevicePowerfulEnough(mediaPerformanceClass: Int): Boolean {
        if (mediaPerformanceClass >= Build.VERSION_CODES.S) {
            Log.d("PerfCheck", "Passed via Performance Class: $mediaPerformanceClass")
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
            Log.e("PerfCheck", "Failed to read RAM for fallback", e)
        }

        Log.d(
            "PerfCheck",
            "PerfClass was $mediaPerformanceClass. Fallback -> Cores: $cores | RAM: ${String.format("%.2f", totalRamGb)} GB"
        )

        return totalRamGb >= RAM_GB_LIMIT && cores >= CPU_CORES_LIMIT
    }
}