package com.laqoome.laqoo.util

import com.laqoome.laqoo.application.laqooApplication
import com.laqoome.laqoo.data.remote.parse.AgedmSource
import com.laqoome.laqoo.data.remote.parse.AnfunsSource
import com.laqoome.laqoo.data.remote.parse.laqooSource
import com.laqoome.laqoo.data.remote.parse.CyclaqooSource
import com.laqoome.laqoo.data.remote.parse.GirigiriSource
import com.laqoome.laqoo.data.remote.parse.GogolaqooSource
import com.laqoome.laqoo.data.remote.parse.MxdmSource
import com.laqoome.laqoo.data.remote.parse.NyafunSource
import com.laqoome.laqoo.data.remote.parse.SilisiliSource
import com.laqoome.laqoo.data.remote.parse.YhdmSource

object SourceHolder {
    private lateinit var _currentSource: laqooSource
    private lateinit var _currentSourceMode: SourceMode

    /**
     * 默认动漫源
     */
    val DEFAULT_laqoo_SOURCE = SourceMode.Silisili

    val currentSource: laqooSource
        get() = _currentSource

    val currentSourceMode: SourceMode
        get() = _currentSourceMode

    var isSourceChanged = false

    init {
        val preferences = laqooApplication.getInstance().preferences
        initDefaultSource(preferences.getEnum(KEY_SOURCE_MODE, DEFAULT_laqoo_SOURCE))
    }

    /**
     * 初始化加载默认的数据源，切换数据源请用方法[SourceHolder].switchSource()
     */
    private fun initDefaultSource(mode: SourceMode) {
        _currentSource = getSource(mode)
        _currentSourceMode = mode
        _currentSource.onEnter()
    }

    /**
     *切换数据源
     */
    fun switchSource(mode: SourceMode) {
        _currentSource.onExit()

        _currentSource = getSource(mode)
        _currentSourceMode = mode

        _currentSource.onEnter()
    }

    /**
     * 根据[SourceMode]获取对应的[laqooSource]数据源
     * */
    fun getSource(mode: SourceMode): laqooSource {
        return when (mode) {
            SourceMode.Yhdm -> YhdmSource
            SourceMode.Silisili -> SilisiliSource
            SourceMode.Mxdm -> MxdmSource
            SourceMode.Agedm -> AgedmSource
            SourceMode.Anfuns -> AnfunsSource
            SourceMode.Girigiri -> GirigiriSource
            SourceMode.Nyafun -> NyafunSource
            SourceMode.Cyclaqoo -> CyclaqooSource
            SourceMode.Gogolaqoo -> GogolaqooSource
        }
    }
}

enum class SourceMode {
    Silisili,
    Mxdm,
    Girigiri,
    Agedm,
    Cycanime,
    Anfuns,
    Gogoanime,
    Yhdm,
    Nyafun
}