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
        if (metadata == null) {
            return null;
        }

        vamPackage.setName(metadata.getPackageName());
        String version = metadata.getStandardReferenceVersionOption();
        if (version == null) {
            version = "Latest";
        }
        vamPackage.setVersion(version);
        vamPackage.setCreatorName(metadata.getCreatorName());
        vamPackage.setPath(pack.getPath().toString());
        vamPackage.setDescription(metadata.getDescription());
        vamPackage.setImagePath(imagePath);
        vamPackage.setDependencies(JsonUtils.toJson(metadata.getDependencies()));

        return vamPackage;
    }
}
