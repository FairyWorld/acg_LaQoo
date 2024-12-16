package com.laqoome.laqoo.data.remote.parse

import com.laqoome.laqoo.data.remote.dto.laqooBean
import com.laqoome.laqoo.data.remote.dto.laqooDetailBean
import com.laqoome.laqoo.data.remote.dto.HomeBean
import com.laqoome.laqoo.data.remote.dto.VideoBean
import com.laqoome.laqoo.util.preferences

interface laqooSource {

    /**
     * [preferences] 的Key值用于获取用户的自定义的域名
     */
    val KEY_SOURCE_DOMAIN: String
        get() = "${this.javaClass.simpleName}Domain"

    /**
     * 默认动漫域名
     */
    val DEFAULT_DOMAIN: String

    /**
     * 动漫域名，默认值为[DEFAULT_DOMAIN]，
     * 且[DEFAULT_DOMAIN] 要先于 [baseUrl] 初始化
     */
    var baseUrl: String

    suspend fun getHomeData(): List<HomeBean>

    suspend fun getlaqooDetail(detailUrl: String): laqooDetailBean

    suspend fun getVideoData(episodeUrl: String): VideoBean

    suspend fun getSearchData(query: String, page: Int): List<laqooBean>

    suspend fun getWeekData(): Map<Int, List<laqooBean>>

    /**
     * 当切换选中的数据源时调用，可以执行一些初始化操作
     */
    fun onEnter() {}

    /**
     * 当退出当前数据源时调用，可以执行一些清理操作
     */
    fun onExit() {}
}