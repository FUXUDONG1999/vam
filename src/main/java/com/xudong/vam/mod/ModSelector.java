package com.xudong.vam.mod;

import java.io.IOException;

public interface ModSelector {
    void select(String name, long id) throws IOException;

    void clear() throws IOException;
}
