package com.twiceyuan.permissionx.kotlin

import android.app.Activity
import android.support.v4.app.Fragment
import com.twiceyuan.permissionx.PermissionX

fun Activity.requestPermissionX(vararg permissions: String) = PermissionX.request(this, permissions)

fun Fragment.requestPermissionX(vararg permissions: String) = PermissionX.request(this, permissions)

fun Activity.requestPermissionX(permissions: List<String>) = PermissionX.request(this, permissions.toTypedArray())

fun Fragment.requestPermissionX(permissions: List<String>) = PermissionX.request(this, permissions.toTypedArray())
