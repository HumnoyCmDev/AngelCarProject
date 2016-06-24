package com.dollarandtrump.angelcar;

import android.util.Log;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
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
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

/**
 * Created by humnoyDeveloper on 21/4/59. 16:00
 */
public class SampleRx {
    public static void main(String[] str) {

    }

    static class MyObject {

        @MyColumn(name = "COL_NAME")
        private String name;
        @MyColumn(name = "COL_NICKNAME")
        private String nickname;
        @MyColumn(name = "COL_COLOR")
        private String color;

        public MyObject() {

        }

        public MyObject(@MyColumn(name = "pName") String name, String nickname, String color) {
            this.name = name;
            this.nickname = nickname;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }


    @Retention(RetentionPolicy.RUNTIME) //กำหนดว่าใช้เวลา Runtime
    @Target({ElementType.FIELD,ElementType.PARAMETER}) //กำหนดว่าใช้กับ Field
    @interface MyColumn{
        String name();
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
