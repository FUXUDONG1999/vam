package com.xudong.vam.repository;

import com.xudong.vam.model.SelectPackage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectPackageRepository extends CrudRepository<SelectPackage, Long> {
    SelectPackage findByName(String name);
}
