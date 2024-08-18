/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:JvmName("Constants")

package com.example.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log

// Notification Channels

object NotificationChannels {
    // Name and description of the verbose notification channel
    @JvmField val VERBOSE_CHANNEL_NAME: CharSequence = "Verbose WorkManager Notifications"
    const val VERBOSE_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
    const val VERBOSE_CHANNEL_ID = "VERBOSE_NOTIFICATION"
    const val VERBOSE_NOTIFICATION_ID = 1

    // Name and description of the error notification channel
    @JvmField val ERROR_CHANNEL_NAME: CharSequence = "Error Notifications"
    const val ERROR_CHANNEL_DESCRIPTION = "Shows notifications for errors during background work"
    const val ERROR_CHANNEL_ID = "ERROR_NOTIFICATION"
    const val ERROR_NOTIFICATION_ID = 2
}

// Work Names

object WorkNames {
    const val IMAGE_MANIPULATION = "image_manipulation_work"
    const val DATA_PROCESSING = "data_processing_work"
}

// Output Paths and Keys

object OutputKeys {
    const val OUTPUT_PATH = "blur_filter_outputs"
    const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
    const val TAG_OUTPUT = "OUTPUT"
    const val PROCESSING_STATUS = "PROCESSING_STATUS"
}

// Timing Constants

object TimingConstants {
    const val DELAY_TIME_MILLIS: Long = 3000
    const val RETRY_DELAY_TIME_MILLIS: Long = 5000
}

// Utility functions for Notification Channels

/**
 * Creates a notification channel if the Android version is Oreo or higher.
 */
fun createNotificationChannel(context: Context, id: String, name: CharSequence, description: String) {
    // Check if the device is running Android Oreo or higher
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
            this.description = description
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

/**
 * Logs notification creation.
 */
fun logNotificationCreation(id: String) {
    Log.d("NotificationManager", "Notification channel created with ID: $id")
}
