package com.xudong.vam.repository;

import com.xudong.vam.model.SelectDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectDetailRepository extends CrudRepository<SelectDetail, Long> {
    SelectDetail findBySelectIdAndChildId(long selectId, long childId);
}
