package com.twiceyuan.permissionx;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PermissionX {

    private static final String TAG_FRAGMENT = "permissionHolder";

    static Map<Integer, PermissionRequestHolder> sPermissionRequestMap = new LinkedHashMap<>();

    private static AtomicInteger requestCodeGenerator = new AtomicInteger(200);

    /**
     * Request single permission
     *
     * @param activity   permission request host.
     * @param permission the permission you want to request.
     * @return permission request holder
     */
    @SuppressWarnings("WeakerAccess")
    public static PermissionRequestHolder request(Activity activity, String permission) {
        return request(activity, new String[]{permission});
    }

    /**
     * Request single permissions
     *
     * @param activity    permissions request host.
     * @param permissions the permissions you want to request.
     * @return permission request holder
     */
    @SuppressWarnings("WeakerAccess")
    public static PermissionRequestHolder request(Activity activity, String[] permissions) {
        return requestInternal(activity, permissions);
    }

    /**
     * Request single permission
     *
     * @param fragment   permission request host.
     * @param permission the permission you want to request.
     * @return permission request holder
     */
    @SuppressWarnings("WeakerAccess")
    public static PermissionRequestHolder request(Fragment fragment, String permission) {
        return request(fragment, new String[]{permission});
    }

    /**
     * Request single permissions
     *
     * @param fragment    permissions request host.
     * @param permissions the permissions you want to request.
     * @return permission request holder
     */
    @SuppressWarnings("WeakerAccess")
    public static PermissionRequestHolder request(Fragment fragment, String[] permissions) {
        return requestInternal(fragment, permissions);
    }

    /**
     * Perform request with activity or fragment
     *
     * @param host        permission request host
     * @param permissions the permissions you want to request
     * @return permissions request holder, provide callback to handle the result of requests.
     */
    private static PermissionRequestHolder requestInternal(Object host, String[] permissions) {
        boolean isAllGranted = true;
        HashMap<String, Boolean> permissionGrantedResult = new HashMap<>();

        for (String permission : permissions) {
            Context context;
            if (host instanceof Activity) {
                context = (Context) host;
            } else if (host instanceof Fragment) {
                context = ((Fragment) host).getActivity();
            } else {
                throw checkHostTypeFailedException(host);
            }

            boolean isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
            permissionGrantedResult.put(permission, isGranted);
            if (!isGranted) {
                isAllGranted = false;
            }
        }

        PermissionRequestHolder requestHolder = new PermissionRequestHolder();

        if (isAllGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            requestHolder.handle(true, permissionGrantedResult);
            return requestHolder;
        }

        int requestCode = requestCodeGenerator.getAndIncrement();

        sPermissionRequestMap.put(requestCode, requestHolder);

        if (host instanceof Activity) {
            ((Activity) host).getFragmentManager().beginTransaction()
                    .add(PermissionRequestFragment.newInstance(requestCode, permissionGrantedResult), TAG_FRAGMENT)
                    .commit();
            return requestHolder;
        }

        if (host instanceof Fragment) {
            FragmentManager fragmentManager;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                fragmentManager = ((Fragment) host).getChildFragmentManager();
            } else {
                fragmentManager = ((Fragment) host).getFragmentManager();
            }

            fragmentManager.beginTransaction()
                    .add(PermissionRequestFragment.newInstance(requestCode, permissionGrantedResult), TAG_FRAGMENT)
                    .commit();

            return requestHolder;
        }

        throw checkHostTypeFailedException(host);
    }

    private static RuntimeException checkHostTypeFailedException(Object host) {
        return new RuntimeException("Host(" + host.getClass().getCanonicalName() + ") is not supported");
    }
}
