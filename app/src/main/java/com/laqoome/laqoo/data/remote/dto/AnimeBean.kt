package com.laqoome.laqoo.data.remote.dto

import com.laqoome.laqoo.domain.model.laqoo

/**
 * @param title 动漫名称
 * @param img 图片url         /* 获取时间表时可为空 */
 * @param url 动漫详情url
 * @param episodeName 集数
 */
data class laqooBean(
    val title: String,
    val img: String,
    val url: String,
    val episodeName: String = ""
) {
    fun tolaqoo(): laqoo {
        return laqoo(
            title = title,
            img = img,
            detailUrl = url,
            episodeName = episodeName
        )
    }
}