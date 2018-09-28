package com.twiceyuan.permissionx.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.twiceyuan.commonadapter.library.adapter.CommonListAdapter;
import com.twiceyuan.permissionx.PermissionX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 需要测试该部分将 Manifest 中的配置替换掉
 */
@SuppressLint("Registered")
public class MainActivity extends AppCompatActivity {

    private Map<String, Boolean> permissionCheckStatus = new HashMap<>();

    // 示例的权限列表
    private List<String> permissions = Arrays.asList(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_SMS,
            Manifest.permission.CALL_PHONE
    );

    // 显示权限列表的适配器
    private CommonListAdapter<String, PermissionItemViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initStatus();
    }

    @SuppressLint("MissingPermission")
    private void requestPermission() {

        // 临时存储需要请求的权限
        List<String> todoRequestPermissions = new ArrayList<>();

        // 根据权限的选择情况过滤
        for (Map.Entry<String, Boolean> entry : permissionCheckStatus.entrySet()) {
            if (entry.getValue()) {
                todoRequestPermissions.add(entry.getKey());
            }
        }

        PermissionX.request(this, todoRequestPermissions.toArray(new String[0]))
                .onGranted(() -> {
                    adapter.notifyDataSetChanged();
                    messageDialog("所有权限被允许");
                })
                .onDenied(() -> {
                    adapter.notifyDataSetChanged();
                    messageDialog("有权限被拒绝");
                })
                .onRequest((isAllGranted, grantResult) -> {

                    adapter.notifyDataSetChanged();

                    List<String> granted = new ArrayList<>();
                    List<String> denied = new ArrayList<>();

                    for (Map.Entry<String, Boolean> entry : grantResult.entrySet()) {
                        String permissionName = getPermissionName(entry.getKey());
                        if (entry.getValue()) {
                            granted.add(permissionName);
                        } else {
                            denied.add(permissionName);
                        }
                    }

                    messageDialog(String.format("允许的权限：\n%s\n\n拒绝的权限：%s", granted, denied));
                });
    }

    private void initStatus() {
        for (String permission : permissions) {
            permissionCheckStatus.put(permission, false);
        }

        adapter = new CommonListAdapter<>(this, PermissionItemViewHolder.class);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        adapter.setOnBindListener((parentView, position, permission, holder) -> {

            final Boolean isChecked = permissionCheckStatus.get(permission);
            holder.mCheckBox.setChecked(isChecked == null ? false : isChecked);
            holder.mCheckBox.setText(getPermissionName(permission));
            boolean isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
            holder.mTvStatus.setText(isGranted ? "已允许" : "未允许");

            holder.getItemView().setOnClickListener((v) -> {
                Boolean isCheckedInner = permissionCheckStatus.get(permission);
                isCheckedInner = isCheckedInner == null ? false : isCheckedInner;
                permissionCheckStatus.put(permission, !isCheckedInner);
                adapter.notifyDataSetChanged();
            });
        });

        adapter.addAll(permissions);
        adapter.notifyDataSetChanged();

        findViewById(R.id.btn_request).setOnClickListener(v -> requestPermission());
    }

    /**
     * 测试用，弹出一个对话框
     *
     * @param message 对话框消息
     */
    private void messageDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .show();
    }

    /**
     * 测试用，获取权限的名称信息
     *
     * @param permission 权限字符串常量
     * @return 权限信息对象
     */
    @NonNull
    private String getPermissionName(@NonNull String permission) {
        try {
            PackageManager manager = getPackageManager();
            return (String) manager.getPermissionInfo(permission, PackageManager.GET_META_DATA).loadLabel(manager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
