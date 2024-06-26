package com.xudong.vam.core.model

import jakarta.persistence.*

@Entity
@Table(name = "t_select_detail")
data class SelectDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "select_id")
    var selectId: Long = 0,

    @Column(name = "root_id")
    var rootId: Long = 0,

    @Column(name = "child_id")
    var childId: Long = 0,
)