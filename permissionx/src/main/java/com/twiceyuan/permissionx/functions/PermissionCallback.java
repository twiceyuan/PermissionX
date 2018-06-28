package com.twiceyuan.permissionx.functions;

import java.util.Map;

public interface PermissionCallback {

    void call(boolean isAllGranted, Map<String, Boolean> grantResult);
}
