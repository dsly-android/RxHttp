package com.android.dsly.rxhttp;

/**
 *
 * @author 陈志鹏
 * @date 2018/9/14
 */
public interface IView {
    /**
     * 显示加载
     */
    void showLoading();

    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 杀死自己
     */
    void killMyself();
}
