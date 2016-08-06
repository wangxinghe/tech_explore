package com.mouxuejie.fun.sample;

import android.util.Log;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangxinghe on 26/3/2016.
 */
public class RxJavaSample {

    private static final String TAG = RxJavaSample.class.getSimpleName();

    private void testObserver() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + e);
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext -> " + s);
            }
        };
    }

    private void testSubscriber() {
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + e);
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext -> " + s);
            }
        };
    }

    private void testObservable() {
        Observable<String> observable0 = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("message 1");
                subscriber.onNext("message 2");
                subscriber.onCompleted();
            }
        });

        Observable<String> observable1 = Observable.just("message 1", "message 2");

        String[] array = {"message 1", "message 2"};
        Observable<String> observable2 = Observable.from(array);
    }

    public void testScheduler() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Log.d(TAG, "OnSubscribe.call Thread -> " + Thread.currentThread().getName());
                subscriber.onNext("message");
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Subscriber<String>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {

              }

              @Override
              public void onNext(String s) {
                  Log.d(TAG, "Subscriber.onNext Thread -> " + Thread.currentThread().getName());
              }
          });
    }

    /**
     * map: Person -> id(String)
     * 打印某个人id
     */
    private void testMap0() {
        Observable.just(getPersonArray()[0])
                .map(new Func1<Person, String>() {
                    @Override
                    public String call(Person person) {
                        return person.id;
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String id) {
                        Log.d(TAG, "id -> " + id);
                    }
                });
    }

    /**
     * map: array Person -> id(String)
     * 打印每个人的id
     */
    private void testMap() {
        Observable.from(getPersonArray())
                .map(new Func1<Person, String>() {
                    @Override
                    public String call(Person person) {
                        return person.id;
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String id) {
                        Log.d(TAG, "id -> " + id);
                    }
                });
    }

    /**
     * flatMap: array Person -> email数组（String[]）
     * 打印每个人的所有email
     */
    private void testFlatMap() {
        Observable.from(getPersonArray())
                .flatMap(new Func1<Person, Observable<Person.Email>>() {
                    @Override
                    public Observable<Person.Email> call(Person person) {
                        Log.d(TAG, "flatMap " + person.id);
                        return Observable.from(person.emails);
                    }
                })
                .subscribe(new Subscriber<Person.Email>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError " + e.getMessage());
                    }

                    @Override
                    public void onNext(Person.Email email) {
                        Log.d(TAG, "onNext " + email.name);
                    }
                });
    }

    private Person[] getPersonArray() {
        Person[] persons = new Person[2];
        for (int i = 0; i < persons.length; i++) {
            Person.Email[] emails = new Person.Email[2];
            for (int j = 0; j < emails.length; j++) {
                emails[j] =new Person.Email("email " + j);
            }
            persons[i] = new Person("person" + i, emails);
        }
        return persons;
    }

    public static class Person {
        public String id;
        public Email[] emails;

        public Person(String id, Email[] emails) {
            this.id = id;
            this.emails = emails;
        }

        public static class Email {
            public String name;

            public Email(String name) {
                this.name = name;
            }
        }
    }

}
