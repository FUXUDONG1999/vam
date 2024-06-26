package com.xudong.vam.service.impl;

import com.xudong.vam.model.SelectPackage;
import com.xudong.vam.repository.SelectDetailRepository;
import com.xudong.vam.repository.SelectPackageRepository;
import com.xudong.vam.service.SelectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SelectServiceImpl implements SelectService {
    private final SelectPackageRepository selectPackageRepository;

    private final SelectDetailRepository selectDetailRepository;

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
