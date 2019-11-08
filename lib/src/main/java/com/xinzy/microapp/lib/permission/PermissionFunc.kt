package com.xinzy.microapp.lib.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

internal fun getDeniedPermissions(permissions: Array<String>, grantResults: IntArray): Array<String> =
        permissions.filterIndexed { index, s ->
            grantResults[index] == PackageManager.PERMISSION_DENIED
        }.toTypedArray()

internal fun getPermanentlyDeniedPermissions(fragment: Fragment, permissions: Array<String>, grantResults: IntArray): Array<String> =
        permissions.filterIndexed { index, s ->
            grantResults[index] == PackageManager.PERMISSION_DENIED && !fragment.shouldShowRequestPermissionRationale(s)
        }.toTypedArray()

/**
 * Returns true if the Activity has access to all given permissions.
 * Always returns true on platforms below M.
 *
 * @see Activity.checkSelfPermission
 */
internal fun hasSelfPermission(activity: Context?, permissions: Array<String>): Boolean {
    // Verify that all required permissions have been granted
    activity?.let {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
    }

    return true
}


data class QuickPermissionsOptions(
    var handleRationale: Boolean = true,
    var rationaleMessage: String = "",
    var handlePermanentlyDenied: Boolean = true,
    var permanentlyDeniedMessage: String = "",
    var rationaleMethod: ((QuickPermissionsRequest) -> Unit)? = null,
    var permanentDeniedMethod: ((QuickPermissionsRequest) -> Unit)? = null,
    var permissionsDeniedMethod: ((QuickPermissionsRequest) -> Unit)? = null
)

data class QuickPermissionsRequest(
    private var target: PermissionCheckerFragment,
    var permissions: Array<String> = emptyArray(),
    var handleRationale: Boolean = true,
    var rationaleMessage: String = "",
    var handlePermanentlyDenied: Boolean = true,
    var permanentlyDeniedMessage: String = "",
    internal var rationaleMethod: ((QuickPermissionsRequest) -> Unit)? = null,
    internal var permanentDeniedMethod: ((QuickPermissionsRequest) -> Unit)? = null,
    internal var permissionsDeniedMethod: ((QuickPermissionsRequest) -> Unit)? = null,
    var deniedPermissions: Array<String> = emptyArray(),
    var permanentlyDeniedPermissions: Array<String> = emptyArray()
) {
    /**
     * Proceed with requesting permissions again with user request
     */
    fun proceed() = target.requestPermissionsFromUser()

    /**
     * Cancels the current permissions request flow
     */
    fun cancel() = target.clean()

    /**
     * In case of permissions permanently denied, request user to enable from app settings
     */
    fun openAppSettings() = target.openAppSettings()
}