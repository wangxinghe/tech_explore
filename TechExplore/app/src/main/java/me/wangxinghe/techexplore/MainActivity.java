package me.wangxinghe.techexplore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import me.wangxinghe.techexplore.api.GithubAPI;
import me.wangxinghe.techexplore.bean.UserInfo;
import me.wangxinghe.techexplore.sample.RxJavaSample;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private Button mButton;
    private TextView mTextView;

    //
    private RxJavaSample mRxJavaSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText)findViewById(R.id.edit_text);
        mButton = (Button)findViewById(R.id.button);
        mTextView = (TextView)findViewById(R.id.text_view);

        mRxJavaSample = new RxJavaSample();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRxJavaSample.testScheduler();
            }
        });
    }

    private void viewRx() {
        RxView.clicks(mButton)
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {
                        Toast.makeText(MainActivity.this, "click me", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserInfo() {
        String username = mEditText.getText().toString();
        new RestAdapter.Builder()
                .setEndpoint(GithubAPI.HOST)
                .build()
                .create(GithubAPI.class)
                .getUserInfo(username, new Callback<UserInfo>() {
                    @Override
                    public void success(UserInfo userInfo, Response response) {
                        mTextView.setText(userInfo.email);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mTextView.setText(error.getMessage());
                    }
                });

    }

    private void fetchUserInfoRx() {
        String username = mEditText.getText().toString();
        Observable<UserInfo> observable = new RestAdapter.Builder()
                .setEndpoint(GithubAPI.HOST)
                .setClient(new OkClient())
                .build()
                .create(GithubAPI.class)
                .getUserInfo(username);
        observable.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mTextView.setText(e.getMessage());
                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        mTextView.setText(userInfo.email);
                    }
                });
    }

    private GithubAPI getGithubAPI() {
        return new RestAdapter.Builder()
                .setEndpoint(GithubAPI.HOST)
                .setClient(new OkClient())
                .build()
                .create(GithubAPI.class);
    }

    private void processUserInfo(UserInfo userInfo) {
        userInfo.email = "hhhhh@gmail.com";
    }

    private void fetchUserInfoRxDoOnNext() {
        String username = mEditText.getText().toString();

        getGithubAPI()
            .getUserInfo(username)
            .doOnNext(new Action1<UserInfo>() {
                @Override
                public void call(UserInfo userInfo) {
                    Log.d("wxh", "processUserInfo");
                    processUserInfo(userInfo);
                }
            })
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<UserInfo>() {
                @Override
                public void onCompleted() {
                    Log.d("wxh", "onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    Log.d("wxh", "onError " + e.getMessage());
                    mTextView.setText(e.getMessage());
                }

                @Override
                public void onNext(UserInfo userInfo) {
                    Log.d("wxh", "onNext");
                    mTextView.setText(userInfo.email);
                }
            });
    }

    private void fetchUserInfoRxFlatMap() {
        final String username = mEditText.getText().toString();

        getGithubAPI()
                .getUserInfo(username)
                .flatMap(new Func1<UserInfo, Observable<String>>() {
                    @Override
                    public Observable<String> call(UserInfo userInfo) {
                        Log.d("wxh", "flatMap" + userInfo.name);
                        return Observable.just(userInfo.name);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d("wxh", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("wxh", "onError " + e.getMessage());
                        mTextView.setText(e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("wxh", "onNext");
                        mTextView.setText(s);
                    }
                });
    }



}
