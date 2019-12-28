package com.android.dsly.rxhttp.utils;

import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle3.components.support.RxFragment;

import io.reactivex.annotations.NonNull;

/**
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
     * @param provider
     * @param event
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final LifecycleProvider provider,
                                                             final ActivityEvent event) {
        if (provider instanceof RxAppCompatActivity) {
            return ((RxAppCompatActivity) provider).bindUntilEvent(event);
        } else {
            throw new IllegalArgumentException("view isn't RxAppCompatActivity");
        }
    }

    /**
     * 绑定 Fragment 的指定生命周期
     *
     * @param provider
     * @param event
     * @param <T>
     * @return
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final LifecycleProvider provider,
                                                             final FragmentEvent event) {
        if (provider instanceof RxFragment) {
            return ((RxFragment) provider).bindUntilEvent(event);
        } else {
            throw new IllegalArgumentException("view isn't RxFragment");
        }
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(final LifecycleProvider provider) {
        if (provider instanceof RxAppCompatActivity) {
            return ((RxAppCompatActivity) provider).bindToLifecycle();
        } else if (provider instanceof RxFragment) {
            return ((RxFragment) provider).bindToLifecycle();
        } else {
            throw new IllegalArgumentException("Lifecycleable not match");
        }
    }

    public static <T> LifecycleTransformer<T> bindUntilDestroyEvent(final LifecycleProvider provider) {
        if (provider instanceof RxAppCompatActivity) {
            return ((RxAppCompatActivity) provider).bindUntilEvent(ActivityEvent.DESTROY);
        } else if (provider instanceof RxFragment) {
            return ((RxFragment) provider).bindUntilEvent(FragmentEvent.DESTROY);
        } else {
            throw new IllegalArgumentException("Lifecycleable not match");
        }
    }
}
