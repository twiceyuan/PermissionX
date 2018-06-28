package com.twiceyuan.permissionx;

import android.support.annotation.NonNull;

import com.twiceyuan.permissionx.functions.PermissionCallback;
import com.twiceyuan.permissionx.functions.PermissionOnDenied;
import com.twiceyuan.permissionx.functions.PermissionOnGranted;

import java.util.Map;

public class PermissionRequestHolder {

    private PermissionCallback  mPermissionCallback;
    private PermissionOnGranted mPermissionOnGranted;
    private PermissionOnDenied  mPermissionOnDenied;

    private LazyResult mLazyResult = null;

    protected void handle(boolean isAllGranted, Map<String, Boolean> grantedDetails) {

        mLazyResult = new LazyResult(isAllGranted, grantedDetails);

        if (mPermissionCallback != null) {
            mPermissionCallback.call(isAllGranted, grantedDetails);
        }

        if (mPermissionOnGranted != null && isAllGranted) {
            mPermissionOnGranted.onAllGranted();
        }

        if (mPermissionOnDenied != null && !isAllGranted) {
            mPermissionOnDenied.onAnyDenied();
        }
    }

    public PermissionRequestHolder onGranted(@NonNull PermissionOnGranted onGranted) {
        if (mLazyResult != null) {
            if (mLazyResult.isAllGranted) {
                onGranted.onAllGranted();
            }
        } else {
            mPermissionOnGranted = onGranted;
        }
        return this;
    }

    public PermissionRequestHolder onDenied(@NonNull PermissionOnDenied onDenied) {
        if (mLazyResult != null) {
            if (!mLazyResult.isAllGranted) {
                onDenied.onAnyDenied();
            }
        } else {
            mPermissionOnDenied = onDenied;
        }
        return this;
    }

    public PermissionRequestHolder onRequest(@NonNull PermissionCallback callback) {
        if (mLazyResult != null) {
            callback.call(mLazyResult.isAllGranted, mLazyResult.grantedDetails);
        } else {
            mPermissionCallback = callback;
        }
        return this;
    }

    private static class LazyResult {
        boolean              isAllGranted;
        Map<String, Boolean> grantedDetails;

        public LazyResult(boolean isAllGranted, Map<String, Boolean> grantedDetails) {
            this.isAllGranted = isAllGranted;
            this.grantedDetails = grantedDetails;
        }
    }
}
