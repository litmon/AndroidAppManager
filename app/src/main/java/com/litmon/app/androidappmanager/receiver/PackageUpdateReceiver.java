package com.litmon.app.androidappmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import com.litmon.app.androidappmanager.model.AppData;

public class PackageUpdateReceiver extends BroadcastReceiver {

    public PackageUpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        String packageName = intent.getDataString().substring("package:".length());

        switch (action){
            case Intent.ACTION_PACKAGE_ADDED:
                // Install App
                Log.d("AppManager", "Received Package Installed.");

                break;

            case Intent.ACTION_PACKAGE_REMOVED:
                boolean isReplacing = intent.getExtras().getBoolean(Intent.EXTRA_REPLACING);

                if(!isReplacing){
                    // Update App
                    Log.d("AppManager", "Received Package Updated.");

                }else{
                    // Uninstall Update
                    Log.d("AppManager", "Received Package Update Uninstalled.");

                }

                break;

            case Intent.ACTION_PACKAGE_FULLY_REMOVED:
                // Uninstall App
                Log.d("AppManager", "Received Package Uninstalled.");

                AppData appData = AppData.findByPackage(packageName);

                if(appData != null){
                    Log.d("AppManager", "AppData Deleted: " + appData);
                    appData.delete();
                }

                break;

        } /* End of switch(action) */

    } /* End of onReceived */

    public IntentFilter getIntentFilter(){
        IntentFilter filter = new IntentFilter();

        filter.addDataScheme("package");
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);

        return filter;
    }

    public interface OnPackageUpdateListener {
        void onPackageInstalled(String packageName);
        void onPackageUninstalled(String packageName);

        void onPackageUpdated(String packageName);
        void onPackageUpdateUninstalled(String packageName);
    }
}
