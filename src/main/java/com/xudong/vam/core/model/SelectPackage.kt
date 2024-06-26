package com.xudong.vam.core.model

import jakarta.persistence.*

@Entity
@Table(name = "t_select_package")
data class SelectPackage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", length = 128)
    var name: String = "",

    @Column(name = "uuid")
    var uuid: String = "",
)