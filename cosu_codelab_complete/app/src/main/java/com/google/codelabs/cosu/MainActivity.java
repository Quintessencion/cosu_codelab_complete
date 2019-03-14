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
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends Activity {

    private PackageManager mPackageManager;

    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        mAdminComponentName = DeviceAdminReceiver.getComponentName(this);

        mPackageManager = getPackageManager();

        findViewById(R.id.start_lock_button).setOnClickListener(v -> {
//            if (mDevicePolicyManager.isDeviceOwnerApp(getApplicationContext().getPackageName())) {
            Intent lockIntent = new Intent(getApplicationContext(), LockedActivity.class);

            mPackageManager.setComponentEnabledSetting(
                    new ComponentName(getApplicationContext(),
                            LockedActivity.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            startActivity(lockIntent);
            finish();
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        R.string.not_lock_whitelisted, Toast.LENGTH_SHORT)
//                        .show();
//            }
        });

        // Check to see if started by LockActivity and disable LockActivity if so

        Intent intent = getIntent();

        if (intent.getIntExtra(LockedActivity.LOCK_ACTIVITY_KEY, 0) == LockedActivity.FROM_LOCK_ACTIVITY) {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(mAdminComponentName, getPackageName());
            mPackageManager.setComponentEnabledSetting(
                    new ComponentName(getApplicationContext(), LockedActivity.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
