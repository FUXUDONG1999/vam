package com.xudong.vam.repository;

import com.xudong.vam.model.VamPackageUsage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VamPackageUsageRepository extends CrudRepository<VamPackageUsage, Long> {
    List<VamPackageUsage> findAllByDependencyId(Long id);

    List<VamPackageUsage> findAllByRootId(Long id);
}
