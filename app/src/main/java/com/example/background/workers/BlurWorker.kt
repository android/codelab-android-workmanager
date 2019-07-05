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

package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri

import com.example.background.KEY_IMAGE_URI

import java.io.FileNotFoundException
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import timber.log.Timber

class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val appContext = applicationContext

        // Makes a notification when the work starts and slows down the work so that it's easier to
        // see each WorkRequest start, even on emulated devices
        makeStatusNotification("Blurring image", appContext)
        sleep()

        return try {
            val outputData = createBlurredBitmap(appContext, inputData.getString(KEY_IMAGE_URI))
            Result.success(outputData)
        } catch (fileNotFoundException: FileNotFoundException) {
            Timber.e(fileNotFoundException)
            throw RuntimeException("Failed to decode input stream", fileNotFoundException)
        } catch (throwable: Throwable) {
            // If there were errors, return FAILURE
            Timber.e(throwable)
            Result.failure()
        }
    }

    @Throws(FileNotFoundException::class, IllegalArgumentException::class)
    private fun createBlurredBitmap(appContext: Context, resourceUri: String?): Data {
        if (resourceUri.isNullOrEmpty()) {
            Timber.e("Invalid input uri")
            throw IllegalArgumentException("Invalid input uri")
        }

        val resolver = appContext.contentResolver

        // Create a bitmap
        val bitmap = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri)))

        // Blur the bitmap
        val output = blurBitmap(bitmap, appContext)

        // Write bitmap to a temp file
        val outputUri = writeBitmapToFile(appContext, output)

        // Return the output for the temp file
        return workDataOf(KEY_IMAGE_URI to outputUri.toString())
    }
}