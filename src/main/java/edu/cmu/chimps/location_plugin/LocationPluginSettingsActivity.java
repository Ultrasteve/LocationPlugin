package edu.cmu.chimps.location_plugin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import pub.devrel.easypermissions.EasyPermissions;

public class LocationPluginSettingsActivity extends PreferenceActivity {

    private String[] mPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        testPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void testPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

            if (! EasyPermissions.hasPermissions(this, mPermissions)) {

                EasyPermissions.requestPermissions(this, "Permission ",
                        5699, mPermissions);

            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
