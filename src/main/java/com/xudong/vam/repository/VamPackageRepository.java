package com.xudong.vam.repository;

import com.xudong.vam.model.VamPackage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VamPackageRepository extends CrudRepository<VamPackage, Long> {
    VamPackage findByNameAndVersionAndCreatorName(String name, String version, String creatorName);

    List<VamPackage> findAllByCreatorNameAndName(String creator, String name);
}
