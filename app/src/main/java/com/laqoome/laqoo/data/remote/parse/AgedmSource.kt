package com.laqoome.laqoo.data.remote.parse

import android.annotation.SuppressLint
import com.laqoome.laqoo.data.remote.dto.laqooBean
import com.laqoome.laqoo.data.remote.dto.laqooDetailBean
import com.laqoome.laqoo.data.remote.dto.EpisodeBean
import com.laqoome.laqoo.data.remote.dto.HomeBean
import com.laqoome.laqoo.data.remote.dto.VideoBean
import com.laqoome.laqoo.data.remote.parse.util.WebViewUtil
import com.laqoome.laqoo.util.DownloadManager
import com.laqoome.laqoo.util.getDefaultDomain
import com.laqoome.laqoo.util.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import retrofit2.Response
import java.io.Closeable

@SuppressLint("StaticFieldLeak", "SetJavaScriptEnabled")
object AgedmSource : laqooSource {

    private const val LOG_TAG = "AgedmSource"

    override val DEFAULT_DOMAIN: String = "https://www.agedm.org"

    override var baseUrl: String = getDefaultDomain()

    private val webViewUtil: WebViewUtil by lazy { WebViewUtil() }

    private val filterReqUrl: Array<String> = arrayOf(
        ".css", ".js", ".jpeg", ".svg", ".ico", ".ts",
        ".gif", ".jpg", ".png", ".webp", ".wasm", "age", ".php"
    )

    override fun onExit() {
        webViewUtil.clearWeb()
    }

    override suspend fun getWeekData(): MutableMap<Int, List<laqooBean>> {
        val source = DownloadManager.getHtml(baseUrl)
        val document = Jsoup.parse(source)
        val weekMap = mutableMapOf<Int, List<laqooBean>>()
        document.select("div.text_list_box").select("div.tab-pane")
            .forEachIndexed { index, element ->
                val dayList = mutableListOf<laqooBean>()
                element.select("li").forEach { el ->
                    val title = el.select("a").text()
                    val episodeName = el.select("div.title_sub").text()
                    val url = el.select("a").attr("href")
                    dayList.add(laqooBean(title = title, img = "", url = url, episodeName))
                }
                weekMap[index] = dayList
            }
        return weekMap
    }

    override suspend fun getSearchData(query: String, page: Int): List<laqooBean> {
        val source = DownloadManager.getHtml("$baseUrl/search?query=$query&page=$page")
        val document = Jsoup.parse(source)
        val laqooList = mutableListOf<laqooBean>()
        document.select("div.card").forEach { el ->
            val title = el.select("h5").text()
            val url = el.select("h5 > a").attr("href")
            val imgUrl = el.select("img").attr("data-original")
            laqooList.add(laqooBean(title = title, img = imgUrl, url = url))
        }
        return laqooList
    }

    override suspend fun getHomeData(): List<HomeBean> {
        val source = DownloadManager.getHtml(baseUrl)
        val document = Jsoup.parse(source)

        val homeBeanList = mutableListOf<HomeBean>()
        document.select("div.container").select("div.video_list_box").forEach { element ->
            val title = element.select("h6").text().replace("更多 »", "")
            val moreUrl = element.select("a").attr("href")
            val homeItemBeanList = getlaqooList(element.select("div.video_item"))
            homeBeanList.add(HomeBean(title = title, moreUrl = moreUrl, laqoos = homeItemBeanList))
        }

        return homeBeanList
    }

    override suspend fun getlaqooDetail(detailUrl: String): laqooDetailBean {
        val source = DownloadManager.getHtml(detailUrl)
        val document = Jsoup.parse(source)
        val videoDetailRight = document.select("div.video_detail_right")
        val title = videoDetailRight.select("h2").text()
        val desc = videoDetailRight.select("div.video_detail_desc").text()
        val imgUrl = document.select("div.video_detail_cover > img").attr("data-original")
        val detailBoxList = document.select("div.video_detail_box").select("li")
        val tags = detailBoxList[9].text().split("：")[1].split(" ").toMutableList()
        tags.add(detailBoxList[0].text().split("：")[1])
        tags.add(detailBoxList[1].text().split("：")[1])
        val playlist = document.select("div.tab-content").select("div.tab-pane")
        val channels = getlaqooEpisodes(playlist)
        val relatedlaqoos =
            getlaqooList(document.select("div.video_list_box").select("div.video_item"))
        val laqooDetailBean =
            laqooDetailBean(title, imgUrl, desc, tags, relatedlaqoos, channels = channels)

        return laqooDetailBean
    }

    override suspend fun getVideoData(episodeUrl: String): VideoBean {
        val source = DownloadManager.getHtml(episodeUrl)
        val document = Jsoup.parse(source)
        /*val elements = document.select("div.cata_video_item")
        val title = elements.select("h5").text()
        var episodeName = ""
        val playlist = document.select("div.playlist-source-tab").select("div.tab-pane")
        val episodes = getlaqooEpisodes(playlist, action = { episodeName = it })*/
        val videoUrl = getVideoUrl(document)

        return VideoBean(videoUrl)
    }

    private suspend fun getlaqooList(elements: Elements): List<laqooBean> {
        val laqooList = mutableListOf<laqooBean>()
        elements.forEach { el ->
            val title = el.select("a").text()
            val url = el.select("a").attr("href")
            val imgUrl = el.select("img").attr("data-original")
            val episodeName = el.select("span.video_item--info").text()
            laqooList.add(laqooBean(title = title, img = imgUrl, url = url, episodeName))
        }
        return laqooList
    }

    private suspend fun getlaqooEpisodes(elements: Elements): Map<Int, List<EpisodeBean>> {
        val channels = mutableMapOf<Int, List<EpisodeBean>>()

        val episodes = mutableListOf<EpisodeBean>()
        elements.forEachIndexed { i, e ->
            e.select("li").forEach { el ->
                val name = el.text()
                val url = el.select("a").attr("href")
                episodes.add(EpisodeBean(name, url))
            }
            channels[i] = episodes
        }

        return channels
    }

    private suspend fun getVideoUrl(document: Document): String {

        val videoUrl = document.select("#iframeForVideo").attr("src")

        // 用于判断url的返回类型是否是 video/mp4
        val predicate: suspend (requestUrl: String) -> Boolean = { requestUrl ->
            withContext(Dispatchers.IO) {
                var response: Response<ResponseBody>? = null
                try {
                    "predicate $requestUrl".log(LOG_TAG)
                    response = DownloadManager.request(requestUrl)
                    response.isSuccessful && response.isVideoType()
                } catch (_: Exception) {
                    false
                } finally {
                    response?.closeQuietly()
                }
            }
        }

        return webViewUtil.interceptRequest(
            url = videoUrl,
            regex = ".mp4|.m3u8|video|playurl|hsl|obj|bili",
            predicate = predicate,
            filterRequestUrl = filterReqUrl
        )
    }

    private fun Response<*>.header(key: String): String {
        val header = headers()[key]
        return header ?: ""
    }

    private fun Response<*>.isVideoType(): Boolean {
        return header("Content-Type") == "video/mp4"
    }

    private fun Closeable.closeQuietly() {
        try {
            close()
        } catch (rethrown: RuntimeException) {
            throw rethrown
        } catch (_: Exception) {
        }
    }

    private fun Response<ResponseBody>.closeQuietly() {
        body()?.closeQuietly()
        errorBody()?.closeQuietly()
    }
}

