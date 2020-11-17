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
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.data.BlurredImage
import com.example.background.data.ImagesDatabase
import timber.log.Timber

class BlurWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image", applicationContext)
        sleep()

        return try {
            if (resourceUri.isNullOrEmpty()) {
                Timber.e("Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val outputData = blurAndSaveImage(resourceUri)
            recordImageSaved(resourceUri)
            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            Result.failure()
        }
    }

    private suspend fun recordImageSaved(resourceUri: String) {
        val imageDao = ImagesDatabase.getDatabase(applicationContext).blurredImageDao()
        imageDao.insert(BlurredImage(resourceUri))
    }

    private fun blurAndSaveImage(resourceUri: String): Data {
        val resolver = applicationContext.contentResolver

        val picture = BitmapFactory.decodeStream(
            resolver.openInputStream(Uri.parse(resourceUri)))

        val output = blurBitmap(picture, applicationContext)

        // Write bitmap to a temp file
        val outputUri = writeBitmapToFile(applicationContext, output)

        val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
        return outputData
    }
}
