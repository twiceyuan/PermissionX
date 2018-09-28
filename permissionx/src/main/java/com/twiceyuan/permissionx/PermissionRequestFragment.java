package com.twiceyuan.permissionx;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PermissionRequestFragment extends android.support.v4.app.Fragment {

    public static final String ARG_GRANTED_STATUS = "grantedStatus";
    public static final String ARG_REQUEST_CODE   = "requestCode";

    private HashMap<String, Boolean> mPermissionStatus = new HashMap<>();
    private int mRequestCode;

    public static PermissionRequestFragment newInstance(
            int requestCode,
            @NonNull HashMap<String, Boolean> permissionGrantedStatus
    ) {
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putSerializable(ARG_GRANTED_STATUS, permissionGrantedStatus);
        PermissionRequestFragment fragment = new PermissionRequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection unchecked
        mPermissionStatus = (HashMap<String, Boolean>) getArguments().getSerializable(ARG_GRANTED_STATUS);
        mRequestCode = getArguments().getInt(ARG_REQUEST_CODE);

        assert mPermissionStatus != null;
        ArrayList<String> todoRequest = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : mPermissionStatus.entrySet()) {
            if (!entry.getValue()) {
                todoRequest.add(entry.getKey());
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(todoRequest.toArray(new String[todoRequest.size()]), mRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != mRequestCode) return;

        boolean isAllGranted = true;
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                mPermissionStatus.put(permission, true);
            } else {
                mPermissionStatus.put(permission, false);
                isAllGranted = false;
            }
        }

        PermissionX.sPermissionRequestMap.get(mRequestCode).handle(isAllGranted, mPermissionStatus);
        PermissionX.sPermissionRequestMap.put(mRequestCode, null);
    }
}
