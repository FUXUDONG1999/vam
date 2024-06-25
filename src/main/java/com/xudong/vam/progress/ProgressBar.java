package com.xudong.vam.progress;


@FunctionalInterface
public interface ProgressBar {
    void progress(int total, int current);
}