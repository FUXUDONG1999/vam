package com.xudong.vam.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "t_vam_usage", uniqueConstraints = @UniqueConstraint(name = "uniq", columnNames = {}))
public class VamPackageUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "name", length = 128)
    private String name;

    @NonNull
    @Column(name = "root_id")
    private Long rootId;

    @NonNull
    @Column(name = "dependency_id")
    private Long dependencyId;
}
