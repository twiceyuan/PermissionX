package com.twiceyuan.permissionx.sample

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import com.twiceyuan.commonadapter.library.adapter.CommonListAdapter
import com.twiceyuan.permissionx.kotlin.requestPermissionX
import java.util.*

class MainActivityWithKotlin : AppCompatActivity() {

    private val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_SMS,
            Manifest.permission.CALL_PHONE
    )

    private val permissionCheckStatus = permissions.map { it to false }.toMap(HashMap())

    private val adapter by lazy {
        CommonListAdapter<String, PermissionItemViewHolder>(this, PermissionItemViewHolder::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initStatus()
    }

    @SuppressLint("MissingPermission")
    private fun requestPermission() {

        val todoRequestPermissions = permissionCheckStatus
                .filter { it.value }
                .map { it.key }
                .toList().toTypedArray()

        requestPermissionX(todoRequestPermissions).onGranted {
            adapter.notifyDataSetChanged()
            messageDialog("所有权限被允许")
        }.onDenied {
            adapter.notifyDataSetChanged()
            messageDialog("有权限被拒绝")
        }.onRequest { _, grantResult ->

            adapter.notifyDataSetChanged()

            val granted = ArrayList<String>()
            val denied = ArrayList<String>()

            grantResult.forEach { key, value ->
                val permissionName = getPermissionName(key)
                when {
                    permissionName == null -> return@forEach
                    value == true -> granted.add(permissionName)
                    value == false -> denied.add(permissionName)
                }
            }

            messageDialog(String.format("允许的权限：\n%s\n\n拒绝的权限：%s", granted, denied))
        }
    }

    private fun initStatus() {
        for (permission in permissions) {
            permissionCheckStatus[permission] = false
        }

        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        adapter.setOnBindListener { _, _, permission, holder ->

            holder.mCheckBox.isChecked = permissionCheckStatus[permission] == true
            holder.mCheckBox.text = getPermissionName(permission)
            val isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            holder.mTvStatus.text = if (isGranted) "已允许" else "未允许"

            holder.itemView.setOnClickListener {
                permissionCheckStatus[permission] = !(permissionCheckStatus[permission] ?: false)
                adapter.notifyDataSetChanged()
            }
        }

        adapter.addAll(permissions.toList())
        adapter.notifyDataSetChanged()

        findViewById<View>(R.id.btn_request).setOnClickListener { requestPermission() }
    }

    /**
     * 测试用，弹出一个对话框
     *
     * @param message 对话框消息
     */
    private fun messageDialog(message: String) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .show()
    }

    /**
     * 测试用，获取权限的名称信息
     *
     * @param permission 权限字符串常量
     * @return 权限信息对象
     */
    private fun getPermissionName(permission: String): String? {
        try {
            val manager = packageManager
            return manager.getPermissionInfo(permission, PackageManager.GET_META_DATA).loadLabel(manager) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return ""
    }
}
