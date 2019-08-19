package com.android.dsly.rxhttp.utils;

import com.android.dsly.rxhttp.IView;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle3.components.support.RxFragment;

import io.reactivex.annotations.NonNull;

/**
 *
 * @author dsly
 * @date 2018/9/14
 */
public final class RxLifecycleUtils {

    private RxLifecycleUtils() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    /**
     * 绑定 Activity 的指定生命周期
     *
     * @param view
     * @param event
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final IView view,
                                                             final ActivityEvent event) {
        if (view instanceof RxAppCompatActivity) {
            return ((RxAppCompatActivity) view).bindUntilEvent(event);
        } else {
            throw new IllegalArgumentException("view isn't RxAppCompatActivity");
        }
    }

    /**
     * 绑定 Fragment 的指定生命周期
     *
     * @param view
     * @param event
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final IView view,
                                                             final FragmentEvent event) {
        if (view instanceof RxFragment) {
            return ((RxFragment) view).bindUntilEvent(event);
        } else {
            throw new IllegalArgumentException("view isn't RxFragment");
        }
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(final IView view) {
        if (view instanceof RxAppCompatActivity) {
            return ((RxAppCompatActivity) view).bindToLifecycle();
        } else if (view instanceof RxFragment) {
            return ((RxFragment) view).bindToLifecycle();
        } else {
            throw new IllegalArgumentException("Lifecycleable not match");
        }
    }

    public static <T> LifecycleTransformer<T> bindUntilDestroyEvent(final IView view) {
        if (view instanceof RxAppCompatActivity) {
            return ((RxAppCompatActivity) view).bindUntilEvent(ActivityEvent.DESTROY);
        } else if (view instanceof RxFragment) {
            return ((RxFragment) view).bindUntilEvent(FragmentEvent.DESTROY);
        } else {
            throw new IllegalArgumentException("Lifecycleable not match");
        }
    }
}
