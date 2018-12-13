@file:Suppress("unused")

package com.twiceyuan.permissionx.kotlin

import android.app.Activity
import android.support.v4.app.Fragment
import com.twiceyuan.permissionx.PermissionRequestHolder
import com.twiceyuan.permissionx.PermissionX

fun Activity.requestPermissionX(vararg permissions: String): PermissionRequestHolder =
        PermissionX.request(this, permissions)

fun Fragment.requestPermissionX(vararg permissions: String): PermissionRequestHolder =
        PermissionX.request(this, permissions)

fun Activity.requestPermissionX(permissions: Collection<String>): PermissionRequestHolder =
        PermissionX.request(this, permissions)

fun Fragment.requestPermissionX(permissions: Collection<String>): PermissionRequestHolder =
        PermissionX.request(this, permissions)
