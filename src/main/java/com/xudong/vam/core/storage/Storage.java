package com.xudong.vam.core.storage;

public interface Storage<T> {
    String store(String path, T obj);
}
