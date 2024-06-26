package com.xudong.vam.core.model

import jakarta.persistence.*

@Entity
@Table(name = "t_vam", uniqueConstraints = [UniqueConstraint(name = "uniq", columnNames = ["name", "creator_name", "version"])])
data class VamPackage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "creator_name", nullable = false)
    var creatorName: String = "",

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "version", nullable = false)
    var version: String = "",

    @Column(name = "path", nullable = false, length = 1024)
    var path: String = "",

    @Column(name = "description", length = 4096)
    var description: String? = null,

    @Column(name = "dependencies", length = 100000)
    var dependencies: String? = null,

    @Column(name = "image_path")
    var imagePath: String? = null,
) {
    val fileName: String get() = String.format("%s.%s.%s.var", creatorName, name, version)
}
