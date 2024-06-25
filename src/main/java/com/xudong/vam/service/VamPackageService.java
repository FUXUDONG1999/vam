package com.xudong.vam.service;

import com.xudong.vam.progress.ProgressBar;

import java.io.IOException;
import java.nio.file.Path;

public interface VamPackageService {
    void generate(Path path, ProgressBar progressBar) throws IOException;
}
