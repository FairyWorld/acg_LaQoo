package com.laqoome.laqoo.data.remote.dto

import com.laqoome.laqoo.domain.model.Home

data class HomeBean(
    val title: String,
    val moreUrl: String = "",        // 可为空
    val laqoos: List<laqooBean>
) {
    fun toHome(): Home {
        val homeItems = laqoos.map { it.tolaqoo() }
        return Home(title = title, laqooList = homeItems)
    }
}