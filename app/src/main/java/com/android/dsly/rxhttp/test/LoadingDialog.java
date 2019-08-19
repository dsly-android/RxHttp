package com.android.dsly.rxhttp.test;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/**
 * @author dsly
 * @date 2019-08-12
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        this(context, 0);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
    }
}