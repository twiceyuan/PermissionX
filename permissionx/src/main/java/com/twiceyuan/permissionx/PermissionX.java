package com.twiceyuan.permissionx;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PermissionX {

    private static final String TAG_FRAGMENT = "permissionHolder";

    static Map<Integer, PermissionRequestHolder> sPermissionRequestMap = new LinkedHashMap<>();

    private static AtomicInteger requestCodeGenerator = new AtomicInteger(200);

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
     * @see this#request(Activity, String[])
     */
    public static PermissionRequestHolder request(Activity activity, @NonNull Collection<String> permissions) {
        return requestInternal(activity, permissions.toArray(new String[0]));
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
     * @see this#request(Fragment, String[])
     */
    public static PermissionRequestHolder request(Fragment fragment, @NonNull Collection<String> permissions) {
        return requestInternal(fragment, permissions.toArray(new String[0]));
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

        if (host instanceof FragmentActivity) {
            ((FragmentActivity) host).getSupportFragmentManager().beginTransaction()
                    .add(PermissionRequestFragment.newInstance(requestCode, permissionGrantedResult), TAG_FRAGMENT)
                    .commit();
            return requestHolder;
        }

        if (host instanceof Fragment) {
            FragmentManager fragmentManager;

            fragmentManager = ((Fragment) host).getChildFragmentManager();

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
