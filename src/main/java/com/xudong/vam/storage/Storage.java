package com.xudong.vam.storage;

public interface Storage<T> {
    String store(String path, T obj);
}
