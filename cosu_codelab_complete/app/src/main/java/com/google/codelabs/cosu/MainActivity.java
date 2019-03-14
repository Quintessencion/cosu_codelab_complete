// Copyright 2016 Google Inc.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//      http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codelabs.cosu;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserManager;
import android.widget.Toast;

public class MainActivity extends Activity {

    //adb shell dpm set-device-owner com.google.codelabs.cosu/.AdminReceiver

    private static final int RESULT_ENABLE = 11;

    private PackageManager mPackageManager;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        componentName = new ComponentName(getApplicationContext(), AdminReceiver.class);

        mPackageManager = getPackageManager();

        findViewById(R.id.start_lock_button).setOnClickListener(v -> {
            if (devicePolicyManager.isDeviceOwnerApp(getApplicationContext().getPackageName())) {
                Intent lockIntent = new Intent(MainActivity.this, LockedActivity.class);

                mPackageManager.setComponentEnabledSetting(
                        componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
                startActivity(lockIntent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.not_lock_whitelisted, Toast.LENGTH_SHORT).show();
            }

            devicePolicyManager.addUserRestriction(componentName, UserManager.DISALLOW_ADJUST_VOLUME);
        });

        findViewById(R.id.get_admin_button).setOnClickListener(v -> {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Разрешение на изменение системных клавиш");
                    startActivityForResult(intent, RESULT_ENABLE);
                }
        );

//        devicePolicyManager.setDeviceOwnerLockScreenInfo(componentName, "AdminReceiver is owner");
//        devicePolicyManager.clearPackagePersistentPreferredActivities(componentName, getPackageName());
//        mPackageManager.setComponentEnabledSetting(
//                new ComponentName(getApplicationContext(), LockedActivity.class),
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);
    }
}
