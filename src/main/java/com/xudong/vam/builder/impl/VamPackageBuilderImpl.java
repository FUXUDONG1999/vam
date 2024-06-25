package com.xudong.vam.builder.impl;

import com.xudong.vam.builder.VamPackageBuilder;
import com.xudong.vam.model.VamPackage;
import com.xudong.vam.model.domain.Metadata;
import com.xudong.vam.model.domain.Package;
import com.xudong.vam.utils.JsonUtils;
import org.springframework.stereotype.Component;

@Component
public class VamPackageBuilderImpl implements VamPackageBuilder {
    @Override
    public VamPackage build(Package pack, String imagePath) {
        VamPackage vamPackage = new VamPackage();
        Metadata metadata = pack.getMetadata();
        if (metadata != null) {
            vamPackage.setDescription(metadata.getDescription());
            vamPackage.setDependencies(JsonUtils.toJson(metadata.getDependencies()));
        }

        String fileName = pack.getPath()
                .getFileName()
                .toString();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));

        String[] strings = fileName.split("\\.");
        String creatorName = strings[0];
        String name = strings[1];
        String version = strings[2];

        vamPackage.setName(name);
        vamPackage.setVersion(version);
        vamPackage.setCreatorName(creatorName);
        vamPackage.setPath(pack.getPath().toString());
        vamPackage.setImagePath(imagePath);

        return vamPackage;
    }
}
