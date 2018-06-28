package com.twiceyuan.permissionx.kotlin

import android.app.Activity
import android.app.Fragment
import com.twiceyuan.permissionx.PermissionX

fun Activity.requestPermissionX(permissions: Array<String>) = PermissionX.request(this, permissions)

fun Activity.requestPermissionX(permission: String) = PermissionX.request(this, permission)

fun Fragment.requestPermissionX(permissions: Array<String>) = PermissionX.request(this, permissions)

fun Fragment.requestPermissionX(permission: String) = PermissionX.request(this, permission)