package com.xudong.vam.core.builder;

import com.xudong.vam.core.model.VamPackage;
import com.xudong.vam.core.model.domain.Package;

public interface VamPackageBuilder {
    VamPackage build(Package pack, String imagePath);
}
