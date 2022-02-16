package com.ai.image.interfaces;

public interface ICallBack<T> {

    default void callBack(T value) {
    }

    default void callBack2(T value, T value2) {
    }

    default void callBackItem(T value, int pos) {
    }

}
