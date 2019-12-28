package com.android.dsly.rxhttp.test;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

/**
 * @author 陈志鹏
 * @date 2019-12-27
 */
public class BaseViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> showDialogLiveData = new MutableLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> getShowDialogLiveData() {
        return showDialogLiveData;
    }

    public void setShowDialog(Boolean showDialog) {
        showDialogLiveData.setValue(showDialog);
    }
}
