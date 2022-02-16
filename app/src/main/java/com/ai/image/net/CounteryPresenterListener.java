package com.ai.image.net;

import org.jetbrains.annotations.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Call;

public interface CounteryPresenterListener<T> {
    default void onSuccess(T data) {
    }


    default void onFailure(Call<T> call, Throwable t) {
    }


    default void onRetry(T data) {
    }


    default void onLoading() {
    }

    default void doneLoading() {
    }


    default void onNoWifi() {
    }


    default void onNoData() {
    }

    //for items recyclerview loading
    default void onSuccess(T data, int pos) {
    }

    default void onFailure(Call<T> call, Throwable t, int pos) {
    }

    default void onRetry(T data, int pos) {
    }

    default void onLoading(int pos) {
    }

    default void doneLoading(int pos) {
    }

    default void onNoWifi(int pos) {
    }

    default void onErrorBody(@Nullable ResponseBody errorBody) {
    }

    default void onErrorBody(@Nullable ResponseBody errorBody, int pos) {
    }

}