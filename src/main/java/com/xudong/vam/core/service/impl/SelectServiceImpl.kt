package com.xudong.vam.core.service.impl;

import com.xudong.vam.core.model.SelectPackage;
import com.xudong.vam.core.repository.SelectPackageRepository;
import com.xudong.vam.core.service.SelectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SelectServiceImpl implements SelectService {
    private final SelectPackageRepository selectPackageRepository;

    @Override
    public long createSelect(String name) {
        SelectPackage selectPackage = selectPackageRepository.findByName(name);
        if (selectPackage != null) {
            return selectPackage.getId();
        }

        selectPackage = new SelectPackage(name, UUID.randomUUID().toString());
        selectPackage = selectPackageRepository.save(selectPackage);

        return selectPackage.getId();
    }
}
