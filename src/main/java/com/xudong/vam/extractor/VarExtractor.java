package com.xudong.vam.extractor;

import com.xudong.vam.model.domain.Package;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface VarExtractor {
    Package extract(Path packagePath) throws IOException;

    List<CompletableFuture<Package>> extractAll(Path packagePath) throws IOException;
}
