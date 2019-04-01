/*
 * Copyright (C) 2019 The Android Open Source Project
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
import com.example.background.OUTPUT_PATH
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.UUID

/**
 * Copy a file from the asset folder in the testContext to the OUTPUT_PATH in the target context.
 * @param testCtx android test context
 * @param targetCtx target context
 * @param filename source asset file
 * @return Uri for temp file
 */
@Throws(Exception::class)
fun copyFileFromTestToTargetCtx(testCtx: Context, targetCtx: Context, filename: String): Uri {
    // Create test image
    val destinationFilename = String.format("blur-test-%s.png", UUID.randomUUID().toString())
    val outputDir = File(targetCtx.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val outputFile = File(outputDir, destinationFilename)

    val bis = BufferedInputStream(testCtx.assets.open(filename))
    val bos = BufferedOutputStream(FileOutputStream(outputFile))
    val buf = ByteArray(1024)
    bis.read(buf)
    do {
        bos.write(buf)
    } while (bis.read(buf) != -1)
    bis.close()
    bos.close()

    return Uri.fromFile(outputFile)
}

/**
 * Check if a file exist in the given context.
 * @param testCtx android test context
 * @param uri for the file
 * @return true if file exist, false if the file does not exist of the Uri is not valid
 */
fun uriFileExists(targetCtx: Context, uri: String?): Boolean {
    if (uri.isNullOrEmpty()) {
        return false
    }

    val resolver = targetCtx.contentResolver

    // Create a bitmap
    try {
        BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(uri)))
    } catch (e: FileNotFoundException) {
        return false
    }
    return true
}