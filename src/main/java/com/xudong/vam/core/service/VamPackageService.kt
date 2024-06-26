package com.xudong.vam.core.service;

import com.xudong.vam.core.progress.ProgressBar;

import java.io.IOException;
import java.nio.file.Path;

public interface VamPackageService {
    void generate(Path path, ProgressBar progressBar) throws IOException;
}