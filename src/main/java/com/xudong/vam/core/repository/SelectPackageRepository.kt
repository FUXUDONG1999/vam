package com.xudong.vam.core.repository;

import com.xudong.vam.core.model.SelectPackage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectPackageRepository extends CrudRepository<SelectPackage, Long> {
    SelectPackage findByName(String name);
}
