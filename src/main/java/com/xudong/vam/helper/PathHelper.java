package com.xudong.vam.helper;

import com.xudong.vam.config.VamProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@AllArgsConstructor
public class PathHelper {
    private VamProperties vamProperties;

    public Path getSelectPath(String uuid) {
        return Path.of(vamProperties.getGamePath(), "vam-packages-link", uuid);
    }
}
