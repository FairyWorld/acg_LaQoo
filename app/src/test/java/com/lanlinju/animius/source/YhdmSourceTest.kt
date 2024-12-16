package com.laqoome.laqoo.source

import com.laqoome.laqoo.data.remote.api.laqooApiImpl
import com.laqoome.laqoo.data.remote.parse.YhdmSource.baseUrl
import com.laqoome.laqoo.util.DownloadManager
import com.laqoome.laqoo.util.SourceMode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class YhdmSourceTest {

    val api = laqooApiImpl()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_network() {
        runBlocking {
            val html = DownloadManager.getHtml(baseUrl)
            println(html)
        }
    }

    @Test
    fun test_detail() {
        runBlocking {
            println(api.getlaqooDetail("5042.html", SourceMode.Yhdm))
        }
    }


    @Test
    fun test_search() {
        runBlocking {
            val query = "海贼王"
            println(api.getSearchData(query, 1, SourceMode.Yhdm))
        }
    }

    @Test
    fun test_week() {
        runBlocking {
            println(api.getWeekDate())
        }
    }

    @Test
    fun test_dayOfWeek() {
        // 获取当前日期
        val currentDate = LocalDate.now()

        // 获取今天是星期几的数字表示形式（1 表示星期一，2 表示星期二，以此类推）
        val dayOfWeekNumber: Int = currentDate.dayOfWeek.value

        // 输出星期几的数字表示形式
        println("今天是星期：$dayOfWeekNumber")

    }
}