package com.xudong.vam.progress;


@FunctionalInterface
public interface ProgressBar<T> {
    void progress(int total, int current, T item);
}