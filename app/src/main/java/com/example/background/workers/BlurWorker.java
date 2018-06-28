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

package com.example.background.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.background.Constants;

import java.io.FileNotFoundException;
import androidx.work.Data;
import androidx.work.Worker;

public class BlurWorker extends Worker {

    private static final String TAG = BlurWorker.class.getSimpleName();

    @NonNull
    @Override
    public WorkerResult doWork() {

        Context applicationContext = getApplicationContext();

        // Makes a notification when the work starts and slows down the work so that it's easier to
        // see each WorkRequest start, even on emulated devices
        WorkerUtils.makeStatusNotification("Blurring image", applicationContext);
        WorkerUtils.sleep();

        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI, null);
        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri");
                throw new IllegalArgumentException("Invalid input uri");
            }

            ContentResolver resolver = applicationContext.getContentResolver();

            // Create a bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)));

            // Blur the bitmap
            Bitmap output = WorkerUtils.blurBitmap(bitmap, applicationContext);

            // Write bitmap to a temp file
            Uri outputUri = WorkerUtils.writeBitmapToFile(applicationContext, output);

            // Return the output for the temp file
            setOutputData(new Data.Builder().putString(
                    Constants.KEY_IMAGE_URI, outputUri.toString()).build());

            // If there were no errors, return SUCCESS
            return WorkerResult.SUCCESS;
        } catch (FileNotFoundException fileNotFoundException) {
            Log.e(TAG, "Failed to decode input stream", fileNotFoundException);
            throw new RuntimeException("Failed to decode input stream", fileNotFoundException);

        } catch (Throwable throwable) {

            // If there were errors, return FAILURE
            Log.e(TAG, "Error applying blur", throwable);
            return WorkerResult.FAILURE;
        }
    }
}