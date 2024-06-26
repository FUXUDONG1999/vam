package com.xudong.vam.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(name = "t_vam", uniqueConstraints = @UniqueConstraint(name = "uniq", columnNames = {"name", "creator_name", "version"}))
public class VamPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_name", nullable = false)
    private String creatorName;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "path", nullable = false, length = 1024)
    private String path;

    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "dependencies", length = 100000)
    private String dependencies;

    @Column(name = "image_path")
    private String imagePath;

    public String getFileName() {
        return String.format("%s.%s.%s.var", creatorName, name, version);
    }
}
