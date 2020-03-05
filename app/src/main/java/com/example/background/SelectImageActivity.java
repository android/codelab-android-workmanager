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

package com.example.background;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.background.databinding.ActivitySelectBinding;
import java.util.Arrays;
import java.util.List;

public class SelectImageActivity extends AppCompatActivity {

    private static final String TAG = "SelectImageActivity";

    private static final int REQUEST_CODE_IMAGE = 100;
    private static final int REQUEST_CODE_PERMISSIONS = 101;

    private static final String KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT";
    private static final int MAX_NUMBER_REQUEST_PERMISSIONS = 2;

    private static final List<String> sPermissions = Arrays.asList(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    );

    private int mPermissionRequestCount;
    private ActivitySelectBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState != null) {
            mPermissionRequestCount =
                    savedInstanceState.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0);
        }

        // Make sure the app has correct permissions to run
        requestPermissionsIfNecessary();

        // Create request to get image from filesystem when button clicked
        binding.selectImage.setOnClickListener(view -> {
            Intent chooseIntent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(chooseIntent, REQUEST_CODE_IMAGE);
        });
    }

    /** Save the permission request count on a rotate **/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_PERMISSIONS_REQUEST_COUNT, mPermissionRequestCount);
    }

    /**
     * Request permissions twice - if the user denies twice then show a toast about how to update
     * the permission for storage. Also disable the button if we don't have access to pictures on
     * the device.
     */
    private void requestPermissionsIfNecessary() {
        if (!checkAllPermissions()) {
            if (mPermissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                mPermissionRequestCount += 1;
                ActivityCompat.requestPermissions(
                        this,
                        sPermissions.toArray(new String[0]),
                        REQUEST_CODE_PERMISSIONS);
            } else {
                Toast.makeText(this, R.string.set_permissions_in_settings,
                        Toast.LENGTH_LONG).show();
                binding.selectImage.setEnabled(false);
            }
        }
    }

    private boolean checkAllPermissions() {
        boolean hasPermissions = true;
        for (String permission : sPermissions) {
            hasPermissions &=
                    ContextCompat.checkSelfPermission(
                            this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return hasPermissions;
    }

    /** Permission Checking **/

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            requestPermissionsIfNecessary(); // no-op if permissions are granted already.
        }
    }

    /** Image Selection **/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE:
                    handleImageRequestResult(data);
                    break;
                default:
                    Log.d(TAG, "Unknown request code.");
            }
        } else {
            Log.e(TAG, String.format("Unexpected Result code %s", resultCode));
        }
    }

    private void handleImageRequestResult(Intent data) {
        Uri imageUri = null;
        if (data.getClipData() != null) {
            imageUri = data.getClipData().getItemAt(0).getUri();
        } else if (data.getData() != null) {
            imageUri = data.getData();
        }

        if (imageUri == null) {
            Log.e(TAG, "Invalid input image Uri.");
            return;
        }

        Intent filterIntent = new Intent(this, BlurActivity.class);
        filterIntent.putExtra(Constants.KEY_IMAGE_URI, imageUri.toString());
        startActivity(filterIntent);
    }
}
