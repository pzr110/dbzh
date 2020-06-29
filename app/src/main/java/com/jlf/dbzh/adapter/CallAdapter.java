package com.jlf.dbzh.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jlf.dbzh.R;
import com.jlf.dbzh.bean.OnlineBean;

import java.util.List;

public class CallAdapter extends BaseQuickAdapter<OnlineBean.Data.UserList, BaseViewHolder> {

    private boolean isEdit;

    public CallAdapter(@Nullable List<OnlineBean.Data.UserList> data) {
        super(R.layout.item_call, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OnlineBean.Data.UserList item) {
        helper.setText(R.id.tv_name, item.getUsername());

        ImageView mIvSelect = helper.getView(R.id.iv_select);

//        if (isEdit) {
//            mIvSelect.setImageResource(R.mipmap.ic_select);
//            item.setSelect(true);
//        } else {
//            mIvSelect.setImageResource(R.drawable.shape_unselect_circle);
//            item.setSelect(false);
//        }
    }


    public void changeSelectImage(boolean isEdit) {
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }
}
