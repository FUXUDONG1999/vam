package com.xudong.vam.builder;

import com.xudong.vam.model.VamPackage;
import com.xudong.vam.model.domain.Package;

public interface VamPackageBuilder {
    VamPackage build(Package pack, String imagePath);
}
