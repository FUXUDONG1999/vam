package com.xudong.vam.core.progress;


@FunctionalInterface
public interface ProgressBar {
    void progress(int total, int current);
}