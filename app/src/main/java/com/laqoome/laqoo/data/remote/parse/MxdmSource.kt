package com.laqoome.laqoo.data.remote.parse

import com.laqoome.laqoo.data.remote.dto.laqooBean
import com.laqoome.laqoo.data.remote.dto.laqooDetailBean
import com.laqoome.laqoo.data.remote.dto.EpisodeBean
import com.laqoome.laqoo.data.remote.dto.HomeBean
import com.laqoome.laqoo.data.remote.dto.VideoBean
import com.laqoome.laqoo.util.DownloadManager
import com.laqoome.laqoo.util.decryptData
import com.laqoome.laqoo.util.getDefaultDomain
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

object MxdmSource : laqooSource {

    override val DEFAULT_DOMAIN = "https://www.mxdm.tv"
    override var baseUrl = getDefaultDomain()

    override suspend fun getHomeData(): List<HomeBean> {
        val source = DownloadManager.getHtml(baseUrl)
        val document = Jsoup.parse(source)

        val homeBeanList = mutableListOf<HomeBean>()
        document.select("div.module").take(6).forEach { element ->
            val title = element.select("h2").text()
            val moreUrl = element.select("a.more").attr("href")

            val homeItemBeanList = getlaqooList(element.select("div.module-item"))

            homeBeanList.add(HomeBean(title = title, moreUrl = moreUrl, laqoos = homeItemBeanList))
        }

        return homeBeanList
    }

    override suspend fun getlaqooDetail(detailUrl: String): laqooDetailBean {
        val source = DownloadManager.getHtml("$baseUrl/$detailUrl")
        val document = Jsoup.parse(source)

        val main = document.select("main")
        val title = main.select("h1").text()
        val desc = main.select("div.video-info-content").text()
        val imgUrl = main.select("div.module-item-pic > img").attr("data-src")
        val tags = main.select("div.tag-link > a").map { it.text() }
        val channels = getlaqooEpisodes(main)
        val relatedlaqoos =
            getlaqooList(main.select("div.module-items")[0].select("div.module-item"))
        return laqooDetailBean(title, imgUrl, desc, tags, relatedlaqoos, channels = channels)
    }

    override suspend fun getVideoData(episodeUrl: String): VideoBean {
        val source = DownloadManager.getHtml("$baseUrl/${episodeUrl}")
        val document = Jsoup.parse(source)

        /* val elements = document.select("div.video-info-header")
         val title = elements.select("h1 > a").attr("title")*/
        val videoUrl = getVideoUrl(document)

        return VideoBean(videoUrl)
    }

    override suspend fun getSearchData(query: String, page: Int): List<laqooBean> {
        val source = DownloadManager.getHtml("$baseUrl/search/$query----------$page---.html")
        val document = Jsoup.parse(source)

        val laqooList = mutableListOf<laqooBean>()
        document.select("div.module-search-item").forEach { el ->
            val title = el.select("h3").text()
            val url = el.select("h3 > a").attr("href")
            val imgUrl = el.select("img").attr("data-src")
            laqooList.add(laqooBean(title = title, img = imgUrl, url = url))
        }
        return laqooList
    }

    override suspend fun getWeekData(): Map<Int, List<laqooBean>> {
        val source = DownloadManager.getHtml(baseUrl)
        val document = Jsoup.parse(source)

        val elements = document.select("ul.mxoneweek-list")
        val weekMap = mutableMapOf<Int, List<laqooBean>>()
        elements.forEachIndexed { index, element ->
            val dayList = mutableListOf<laqooBean>()
            element.select("li").forEach { el ->
                val title = el.select("a > span")[0].text()
                val episodeName = el.select("a > span")[1].text()
                val url = el.select("a").attr("href")
                dayList.add(laqooBean(title, "", url, episodeName))
            }
            weekMap[index] = dayList
        }
        return weekMap
    }

    private fun getlaqooList(elements: Elements): List<laqooBean> {
        val laqooList = mutableListOf<laqooBean>()
        elements.forEach { el ->
            val title = el.select("a").attr("title")
            val url = el.select("a").attr("href")
            val imgUrl = el.select("img").attr("data-src")
            val episodeName = el.select("div.module-item-text").text()
            laqooList.add(
                laqooBean(
                    title = title,
                    img = imgUrl,
                    url = url,
                    episodeName = episodeName
                )
            )
        }
        return laqooList
    }

    private fun getlaqooEpisodes(elements: Elements): Map<Int, List<EpisodeBean>> {
        val channels = mutableMapOf<Int, List<EpisodeBean>>()
        // 剧集列表,含多条线路
        elements.select("div.module-blocklist > div.scroll-content").forEachIndexed { i, e ->
            val episodes = mutableListOf<EpisodeBean>()
            e.select("a").forEach { el ->
                val name = el.text()
                val url = el.attr("href")
                episodes.add(EpisodeBean(name, url))
            }
            channels[i] = episodes
        }

        return channels
    }

    private const val BASE_M3U8 = "https://danmu.yhdmjx.com/m3u8.php?url="
    private const val AES_KEY = "57A891D97E332A9D"
    private suspend fun getVideoUrl(document: Document): String {
        val urlTarget = document.select("div.player-wrapper > script")[0].data()
        val urlRegex = """"url":"(.*?)","url_next"""".toRegex()
        val url = urlRegex.find(urlTarget)!!.groupValues[1]

        val doc = Jsoup.parse(DownloadManager.getHtml(BASE_M3U8 + url))
        val ivTarget = doc.select("head > script")[1].data()
        val ivRegex = """var bt_token = "(.*?)"""".toRegex()
        val iv = ivRegex.find(ivTarget)!!.groupValues[1]

        val videoUrlTarget = doc.select("body > script")[0].data()
        val videoUrlRegex = """getVideoInfo\("(.*?)"""".toRegex()
        val encryptedVideoUrl = videoUrlRegex.find(videoUrlTarget)!!.groupValues[1]
        return decryptData(encryptedVideoUrl, key = AES_KEY, iv = iv)
    }

}