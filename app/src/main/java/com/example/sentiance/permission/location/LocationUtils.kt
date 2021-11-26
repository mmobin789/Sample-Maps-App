package com.example.sentiance.permission.location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val PERMISSIONS_REQUEST_ACCESS_LOCATION = 33

/**
 * Prompts the user for permission to use the device location if not granted.
 */
fun Activity.hasLocationPermissions(): Boolean {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    return if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        == PackageManager.PERMISSION_GRANTED
    ) {
        true
    } else {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSIONS_REQUEST_ACCESS_LOCATION
        )
        false
    }
}

fun onLocationPermissionResult(
    requestCode: Int,
    grantResults: IntArray,
    onPermission: (Boolean) -> Unit
) {
    onPermission(requestCode == PERMISSIONS_REQUEST_ACCESS_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
}
