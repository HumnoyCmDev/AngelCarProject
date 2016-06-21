package com.dollarandtrump.angelcar;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

/**
 * Created by humnoyDeveloper on 21/4/59. 16:00
 */
public class SampleRx {
    public static void main(String[] str) {
//       List<Integer> integers = new ArrayList<>();
//        integers.add(1);
//        integers.add(2);
//        integers.add(3);
//        integers.add(4);
//        Observable.just(integers).subscribe(new Subscriber<List<Integer>>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(List<Integer> integers) {
//                System.out.println(integers.size());
//            }
//        });

        Observable<Integer> odds = Observable.just(1, 3, 5);
        Observable<String> evens = Observable.just("ewr","wer","dfgd");

        Observable<String> s = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                        subscriber.onNext("sdfsdfsdfsdfsdf");
            }
        });

        Observable.zip(odds, evens, s, new Func3<Integer, String, String, String>() {
            @Override
            public String call(Integer integer, String s, String s2) {
                return null;
            }
        }).groupBy(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                return null;
            }
        });

        Observable.merge(odds,evens,s)
                .subscribe(new Subscriber<Serializable>() {
                    @Override
                    public void onCompleted() {
                        System.out.print("Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.print(e.toString());
                    }

                    @Override
                    public void onNext(Serializable serializable) {
                        System.out.println("Next");
                    }
                });


    }

    static class ModelCar<T> {
        private T t;

        public void set(T t) { this.t = t; }
        public T get() { return t; }
    }

    static class MyClass<T> {

        //Type declaration <T> already done at class level
        private  T myMethod(T a){
            return  a;
        }

        //<T> is overriding the T declared at Class level;
        //So There is no ClassCastException though a is not the type of T declared at MyClass<T>.
        /*private <T> T myMethod1(Object a){
            return (T) a;
        }*/

        //Runtime ClassCastException will be thrown if a is not the type T (MyClass<T>).
        private T myMethod1(Object a){
            return (T) a;
        }

        // No ClassCastException
        // MyClass<String> obj= new MyClass<String>();
        // obj.myMethod2(Integer.valueOf("1"));
        // Since type T is redefined at this method level.
        public  <T> T myMethod2(T a){
            return  a;
        }

        // No ClassCastException for the below
        // MyClass<String> o= new MyClass<String>();
        // o.myMethod3(Integer.valueOf("1").getClass())
        // Since <T> is undefined within this method;
        // And MyClass<T> don't have impact here
        private <T> T myMethod3(Class a){
            return (T) a;
        }

        // ClassCastException for o.myMethod3(Integer.valueOf("1").getClass())
        // Should be o.myMethod3(String.valueOf("1").getClass())
        private  T myMethod4(Class a){
            return (T) a;
        }


        // Class<T> a :: a is Class object of type T
        //<T> is overriding of class level type declaration;
        private <T> Class<T> myMethod5(Class<T> a){
            return  a;
        }
    }
}
