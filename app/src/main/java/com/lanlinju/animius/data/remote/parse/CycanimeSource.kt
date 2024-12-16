package com.laqoome.laqoo.data.remote.parse

import com.laqoome.laqoo.data.remote.dto.laqooBean
import com.laqoome.laqoo.data.remote.dto.laqooDetailBean
import com.laqoome.laqoo.data.remote.dto.EpisodeBean
import com.laqoome.laqoo.data.remote.dto.HomeBean
import com.laqoome.laqoo.data.remote.dto.VideoBean
import com.laqoome.laqoo.data.remote.parse.util.WebViewUtil
import com.laqoome.laqoo.util.DownloadManager
import com.laqoome.laqoo.util.getDefaultDomain
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

object CyclaqooSource : laqooSource {
    // Release page: https://www.cycity.pro/
    override val DEFAULT_DOMAIN: String = "https://www.cyc-laqoo.net"
    override var baseUrl: String = getDefaultDomain()

    private val webViewUtil: WebViewUtil by lazy { WebViewUtil() }

    override fun onExit() {
        webViewUtil.clearWeb()
    }

    private val headers = mapOf("Host" to "www.cyc-laqoo.net")

    override suspend fun getHomeData(): List<HomeBean> {
        val source = DownloadManager.getHtml(baseUrl)
        val document = Jsoup.parse(source)

        val homeBeanList = mutableListOf<HomeBean>()
        document.select("div.box-width.wow")
            .apply { repeat(2) { removeAt(0) } }
            .forEach { element ->
                val title = element.select("h4").text()
                val moreUrl = element.select("a.button").attr("href")
                val homeItemBeanList = getlaqooList(element.select("div.public-list-box"))
                homeBeanList.add(
                    HomeBean(
                        title = title,
                        moreUrl = moreUrl,
                        laqoos = homeItemBeanList
                    )
                )
            }
        return homeBeanList
    }

    override suspend fun getlaqooDetail(detailUrl: String): laqooDetailBean {
        val source = DownloadManager.getHtml("${baseUrl}/$detailUrl")
        val document = Jsoup.parse(source)
        val detailInfo = document.select("div.detail-info")
        val title = detailInfo.select("h3").text()
        val desc = document.select("div#height_limit").text()
        val imgUrl = document.select("div.detail-pic > img").attr("data-src")

        val tags = detailInfo.select("span.slide-info-remarks").map { it.text() }

        val episodes = getlaqooEpisodes(document)
        val relatedlaqoos =
            getlaqooList(document.select("div.box-width.wow").select("div.public-list-box"))
        return laqooDetailBean(title, imgUrl, desc, tags, relatedlaqoos, episodes)
    }

    private fun getlaqooEpisodes(
        document: Document,
        action: (String) -> Unit = {}
    ): List<EpisodeBean> {
        return document.select("div.anthology-list")
            .select("li").map {
                if (it.select("em").isNotEmpty()) {
                    action(it.text())
                }
                EpisodeBean(it.text(), it.select("a").attr("href"))
            }
    }

    override suspend fun getVideoData(episodeUrl: String): VideoBean {
        /* val source = DownloadManager.getHtml("${baseUrl}/$episodeUrl")
         val document = Jsoup.parse(source)
         val title = document.select("div.player-right").select("h2").text()
         var episodeName = ""
         val episodes = getlaqooEpisodes(document, action = { episodeName = it })*/
        val videoUrl = getVideoUrl("$baseUrl/$episodeUrl")
        return VideoBean(videoUrl, headers)
    }

    private suspend fun getVideoUrl(url: String): String {
        return webViewUtil.interceptRequest(
            url = url,
            regex = ".*\\.(mp4|mkv|m3u8).*\\?verify=.*",
        )
    }

    override suspend fun getSearchData(query: String, page: Int): List<laqooBean> {
        val source = DownloadManager.getHtml("${baseUrl}/search/wd/$query/page/$page.html")
        val document = Jsoup.parse(source)
        val laqooList = mutableListOf<laqooBean>()
        document.select("div.public-list-box").forEach { el ->
            val title = el.select("div.thumb-txt").text()
            val url = el.select("a.public-list-exp").attr("href")
            val imgUrl = el.select("img").attr("data-src")
            laqooList.add(laqooBean(title = title, img = imgUrl, url = url))
        }
        return laqooList
    }

    override suspend fun getWeekData(): Map<Int, List<laqooBean>> {
        val source = DownloadManager.getHtml(baseUrl)
        val document = Jsoup.parse(source)
        val weekMap = mutableMapOf<Int, List<laqooBean>>()
        document.select("div#week-module-box")
            .select("div.public-r").forEachIndexed { index, element ->
                val dayList = getlaqooList(element.select("div.public-list-box"))
                weekMap[index] = dayList
            }
        return weekMap
    }

    fun getlaqooList(elements: Elements): List<laqooBean> {
        val laqooList = mutableListOf<laqooBean>()
        elements.forEach { el ->
            val a = el.select("div.public-list-button > a")
            val title = a.text()
            val url = a.attr("href")
            val imgUrl = el.select("img").attr("data-src")
            val episodeName = el.select("div.public-list-subtitle").text()
            laqooList.add(laqooBean(title = title, img = imgUrl, url = url, episodeName))
        }
        return laqooList
    }
}