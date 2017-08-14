package com.yike.coolweather.Gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Fly on 2017/8/8.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }


}
