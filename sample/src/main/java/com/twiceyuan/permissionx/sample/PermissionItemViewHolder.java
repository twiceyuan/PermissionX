package com.twiceyuan.permissionx.sample;

import android.widget.CheckBox;
import android.widget.TextView;

import com.twiceyuan.commonadapter.library.LayoutId;
import com.twiceyuan.commonadapter.library.ViewId;
import com.twiceyuan.commonadapter.library.holder.CommonHolder;

@LayoutId(R.layout.item_permission)
public class PermissionItemViewHolder extends CommonHolder<String> {

    @ViewId(R.id.checkbox)
    CheckBox mCheckBox;

    @ViewId(R.id.tv_status)
    TextView mTvStatus;

    @Override
    public void bindData(String s) {
    }
}
