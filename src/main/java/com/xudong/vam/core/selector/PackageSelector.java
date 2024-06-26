package com.xudong.vam.core.selector;

import java.io.IOException;
import java.util.List;

public interface PackageSelector {
    List<Long> select(long selectId, long rootId) throws IOException;

    void unselect(long selectDetailId) throws IOException;

    void clear() throws IOException;
}
