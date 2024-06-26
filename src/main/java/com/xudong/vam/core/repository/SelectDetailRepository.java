package com.xudong.vam.core.repository;

import com.xudong.vam.core.model.SelectDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectDetailRepository extends CrudRepository<SelectDetail, Long> {
    SelectDetail findBySelectIdAndChildId(long selectId, long childId);
}
