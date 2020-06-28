package com.jlf.dbzh.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jlf.dbzh.R;
import com.jlf.dbzh.bean.OnlineBean;
import com.jlf.dbzh.bean.SoliderBean;

import java.util.List;

public class SoliderAdapter extends BaseQuickAdapter<OnlineBean.Data.UserList, BaseViewHolder> {

    public SoliderAdapter(@Nullable List<OnlineBean.Data.UserList> data) {
        super(R.layout.item_solider, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OnlineBean.Data.UserList item) {
        helper.setText(R.id.tv_name, item.getUsername());
    }
}
