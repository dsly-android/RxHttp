package com.android.dsly.rxhttp.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author dsly
 * @date 2019-08-09
 */
public class GsonFactory {

    public static Gson buildGson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                .registerTypeAdapter(long.class, new LongDefault0Adapter())
                .create();

        return gson;
    }
}
