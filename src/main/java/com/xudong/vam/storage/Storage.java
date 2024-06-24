package com.xudong.vam.storage;

import java.io.IOException;

public interface Storage<T> {
    String store(String path, T obj) throws IOException;
}
