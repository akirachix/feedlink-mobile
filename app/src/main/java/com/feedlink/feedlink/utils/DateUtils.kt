package com.feedlink.feedlink.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    fun parseClaimTime(claimTime: String?): Date {
        if (claimTime == null) return Date(0)

        val formats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        )

        for (format in formats) {
            try {
                if (claimTime.endsWith("Z")) {
                    format.timeZone = TimeZone.getTimeZone("UTC")
                } else {
                    format.timeZone = TimeZone.getDefault()
                }

                val date = format.parse(claimTime)
                if (date != null) {
                    Log.d("DateUtils", "Successfully parsed '$claimTime' with format '${format.toPattern()}'")
                    return date
                }
            } catch (e: Exception) {
                Log.d("DateUtils", "Failed to parse '$claimTime' with format '${format.toPattern()}': ${e.message}")
            }
        }

        Log.e("DateUtils", "Could not parse claim time: $claimTime")
        return Date(0)
    }

    fun formatClaimTime(claimTime: String?): String {
        if (claimTime == null) return "Unknown time"

        return try {
            val claimDate = parseClaimTime(claimTime)
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault()
            dateFormat.format(claimDate)
        } catch (e: Exception) {
            claimTime
        }
    }

    fun formatDuration(claimTime: String?): String {
        if (claimTime == null) return "00:00"

        return try {
            val claimDate = parseClaimTime(claimTime)
            val now = Date()
            val diffInMillis = now.time - claimDate.time

            if (diffInMillis < 0) {
                return "00:00"
            }

            val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
            val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

            return when {
                days >= 7 -> {
                    val weeks = days / 7
                    if (weeks >= 4) {
                        val months = weeks / 4
                        "${months}mo ago"
                    } else {
                        "${weeks}w ago"
                    }
                }
                days >= 1 -> "${days}d ago"
                hours >= 1 -> "${hours}h ago"
                minutes >= 1 -> "${minutes}m ago"
                else -> "Just now"
            }
        } catch (e: Exception) {
            Log.e("DateUtils", "Error formatting duration: ${e.message}")
            "00:00"
        }
    }

    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

    fun formatDeadline(deadline: Date): String {
        val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()
        return outputFormat.format(deadline)
    }
}