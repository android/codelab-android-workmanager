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
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class WorkManagerTestRule : TestWatcher() {
    lateinit var targetContext: Context
    lateinit var testContext: Context
    lateinit var configuration: Configuration
    lateinit var workManager: WorkManager

    override fun starting(description: Description?) {
        targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        testContext = InstrumentationRegistry.getInstrumentation().context
        configuration = Configuration.Builder()
                // Set log level to Log.DEBUG to make it easier to debug
                .setMinimumLoggingLevel(Log.DEBUG)
                // Use a SynchronousExecutor here to make it easier to write tests
                .setExecutor(SynchronousExecutor())
                .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(targetContext, configuration)
        workManager = WorkManager.getInstance()
    }
}