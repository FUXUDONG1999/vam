package com.xudong.vam.service;

import com.xudong.vam.model.VamPackage;
import com.xudong.vam.progress.ProgressBar;

import java.nio.file.Path;

public interface VamPackageService {
    void generate(Path path, ProgressBar<VamPackage> progressBar);
}
