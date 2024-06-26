package com.xudong.vam.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("vam")
public class VamProperties {
    private String imagePath = "E:\\GAME\\vam1.22.0.3\\images";

    private String gamePath = "E:\\GAME\\vam1.22.0.3";

    private String modPath = "E:\\GAME\\vam1.22.0.3\\AddonPackages";
}
