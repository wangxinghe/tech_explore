package me.wangxinghe.techexplore.api;

import me.wangxinghe.techexplore.bean.UserInfo;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by wangxinghe on 23/3/2016.
 */
public interface GithubAPI {

    public static final String HOST = "https://api.github.com";

    @GET("/users/{user}")
    public void getUserInfo(@Path("user") String user, Callback<UserInfo> callback);

    @GET("/users/{user}")
    public Observable<UserInfo> getUserInfo(@Path("user") String user);

}
