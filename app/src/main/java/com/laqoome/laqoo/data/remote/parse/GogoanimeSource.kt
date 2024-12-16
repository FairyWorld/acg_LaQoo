package com.laqoome.laqoo.data.remote.parse

import com.laqoome.laqoo.data.remote.dto.laqooBean
import com.laqoome.laqoo.data.remote.dto.laqooDetailBean
import com.laqoome.laqoo.data.remote.dto.EpisodeBean
import com.laqoome.laqoo.data.remote.dto.HomeBean
import com.laqoome.laqoo.data.remote.dto.VideoBean
import com.laqoome.laqoo.util.DownloadManager
import com.laqoome.laqoo.util.getDefaultDomain
import com.laqoome.laqoo.util.getDocument
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.time.LocalDate

/**
 * reference     https://github.com/consumet/api.consumet.org
 * @webSite      https://consumet-leox-api.vercel.app/laqoo/gogolaqoo
 */
object GogolaqooSource : laqooSource {
    override val DEFAULT_DOMAIN: String = "https://gogolaqoo.by/"
    override var baseUrl: String = getDefaultDomain()

    private var client: HttpClient = DownloadManager.httpClient

    override suspend fun getHomeData(): List<HomeBean> {
        val document = getDocument(baseUrl)
        val homeBeanList = mutableListOf<HomeBean>()
        document.select("div.bixbox").let { element ->
            val title = element.select("h3").text()
            val homeItemList = getlaqooList(element.select("article.bs"))
            homeBeanList.add(HomeBean(title = title, laqoos = homeItemList))
        }
        return homeBeanList
    }

    override suspend fun getlaqooDetail(detailUrl: String): laqooDetailBean {
        val source = DownloadManager.getHtml(detailUrl)
        val document = Jsoup.parse(source)
        val detailInfo = document.select("div.bigcontent")
        val title = detailInfo.select("h1").text()
        val desc = detailInfo.select("p").text()
        val imgUrl = detailInfo.select("img").attr("src")
        val tags = detailInfo.select("div.genxed > a").map { it.text() }
        val episodes = getlaqooEpisodes(document)
        val relatedlaqoos = getlaqooList(document.select("div.listupd > article"))
        return laqooDetailBean(title, imgUrl, desc, tags, relatedlaqoos, episodes)
    }

    private fun getlaqooEpisodes(document: Document): List<EpisodeBean> {
        return document.select("div.episodes-container > div.episode-item > a")
            .map {
                EpisodeBean(it.text(), it.select("a").attr("href"))
            }
    }

    /**
     * @param episodeUrl e.g. https://gogolaqoo.by/dragon-ball-daima-episode-3-english-subbed/
     */
    override suspend fun getVideoData(episodeUrl: String): VideoBean {
        /* val document = getDocument(episodeUrl)
         val result = extractTitleAndEpisode(document.select("h1").text())
         val title = result.first
         var episodeName = result.second
         val allEpisodesUrl = document.select("div.nvs.nvsc > a").attr("href")
         val episodes = getlaqooEpisodes(Jsoup.parse(DownloadManager.getHtml(allEpisodesUrl)))*/
        val regex = Regex(""".*/(.*)-english-subbed/""")
        val episodeId = regex.find(episodeUrl)?.groupValues[1]
            ?: throw IllegalArgumentException("episodeUrl does not match the expected pattern")
        val videoUrl = getVideoUrl(episodeId)
        return VideoBean(videoUrl)
    }


    /**
     * https://consumet-leox-api.vercel.app/laqoo/gogolaqoo/watch/dandadan-episode-6?server=vidstreaming
     * Docs: https://docs.consumet.org/rest-api/laqoo/gogolaqoo/get-laqoo-episode-streaming-links
     */
    private suspend fun getVideoUrl(id: String): String {
        val url = "https://consumet-leox-api.vercel.app/laqoo/gogolaqoo/watch/$id?server=gogocdn"
        val result = client.get(url).body<ResponseData>()
        // 定义按优先级排序的质量列表
        val preferredQualities = listOf("1080p", "720p", "480p", "default")

        // 根据优先级依次查找第一个匹配的 URL
        val videoUrl = preferredQualities
            .asSequence()
            .mapNotNull { quality -> result.sources.find { it.quality == quality }?.url }
            .firstOrNull()

        return videoUrl ?: throw RuntimeException("Video URL is empty")
    }

    /*private fun extractTitleAndEpisode(input: String): Pair<String, String> {
        val regex = Regex("""(.+?) Episode (\d+)""")
        val matchResult = regex.find(input)
        return matchResult!!.let {
            val title = it.groupValues[1].trim()
            val episode = it.groupValues[2].trim()
            Pair(title, episode)
        }
    }*/

    override suspend fun getSearchData(query: String, page: Int): List<laqooBean> {
        val document = getDocument("$baseUrl/page/$page/?s=$query")
        val laqooList = getlaqooList(document.select("div.listupd > article.bs"))
        return laqooList
    }

    override suspend fun getWeekData(): Map<Int, List<laqooBean>> {
        val document = getDocument("$baseUrl/schedule/")
        val weekMap = mutableMapOf<Int, List<laqooBean>>()
        document.select("div.bixbox.schedulepage").forEachIndexed { index, element ->
            val offset = LocalDate.now().dayOfWeek.value - 1
            val dayList = getlaqooList(element.select("div.bs"))
            weekMap[(index + offset).mod(7)] = dayList
        }
        return weekMap
    }

    fun getlaqooList(elements: Elements): List<laqooBean> {
        val laqooList = mutableListOf<laqooBean>()
        elements.forEach { el ->
            val title = el.selectFirst("div.tt")!!.ownText()
            val url = el.select("a").attr("href").extractDetailUrl()
            val imgUrl = el.select("img").attr("src")
            val episodeName = el.select("div.bt > span.epx").text()
            laqooList.add(laqooBean(title = title, img = imgUrl, url = url, episodeName))
        }
        return laqooList
    }

    private fun String.extractDetailUrl(): String {
        if (!this.contains("-episode")) return this
        return this.substringBefore("-episode")
    }

    @Serializable
    data class ResponseData(
        val headers: Headers,
        val sources: List<Source>,
        val download: String
    )

    @Serializable
    data class Headers(
        @SerialName("Referer")
        val referer: String,
        val watchsb: String? = null,
        val userAgent: String? = null
    )

    @Serializable
    data class Source(
        val url: String,
        val quality: String,
        val isM3U8: Boolean
    )
}