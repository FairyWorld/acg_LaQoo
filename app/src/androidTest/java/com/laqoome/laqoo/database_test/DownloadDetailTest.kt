package com.laqoome.laqoo.database_test

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.laqoome.laqoo.data.local.dao.DownloadDao
import com.laqoome.laqoo.data.local.dao.DownloadDetailDao
import com.laqoome.laqoo.data.local.database.laqooDatabase
import com.laqoome.laqoo.data.local.entity.DownloadDetailEntity
import com.laqoome.laqoo.data.local.entity.DownloadEntity
import com.laqoome.laqoo.util.SourceMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DownloadDetailTest {
    private lateinit var downloadDetailDao: DownloadDetailDao
    private lateinit var downloadDao: DownloadDao
    private lateinit var laqooDatabase: laqooDatabase

    private var download =
        DownloadEntity(1, "海贼王1", "/detailUrl1", "/imgUrl1", SourceMode.Yhdm.name, System.currentTimeMillis())
    private val downloadDetail1 = DownloadDetailEntity(
        1, download.downloadId, "第01集", "/imgUrl1",
        1, "/downloadUrl1", "/filePath")
    private val downloadDetail2 = DownloadDetailEntity(
        2, download.downloadId, "第02集", "/imgUrl2", 2,
        "/downloadUrl2", "/filePath1")

    @Before
    fun createDb() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        laqooDatabase = Room.inMemoryDatabaseBuilder(context, laqooDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        downloadDao = laqooDatabase.downLoadDao()
        downloadDetailDao = laqooDatabase.downloadDetailDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        laqooDatabase.close()
    }


    @Test
    fun daoGetDownloadWithDownloadDetails() = runBlocking {
        addTwoDownloadDetailToDB()
        val actual =
            downloadDao.getDownloads().first().fold(0) { acc, d -> acc + d.downloadDetails.size }
        Assert.assertEquals(2, actual)
    }

    @Test
    fun daoGetOneDownloadWithDownloadDetails() = runBlocking {
        addTwoDownloadDetailToDB()
        val actual =
            downloadDao.getDownloadWithDownloadDetails(download.detailUrl)
        Assert.assertNotNull(actual)
    }

    @Test
    fun daoGetDownloadDetail_getOneDownloadDetailFromDB() = runBlocking {
        addOneDownloadDetailToDB()
        val actual = downloadDetailDao.getDownloadDetail(downloadDetail1.downloadUrl)
        Assert.assertNotNull(actual)
    }

    @Test
    fun daoInsert_insertDownloadDetailIntoDB() = runBlocking {
        addTwoDownloadDetailToDB()
        val actual = downloadDetailDao.getDownloadDetails(download.downloadId).first()
        Assert.assertEquals(2, actual.size)
    }

    @Test
    fun daoDeleteOneDownloadDetail_deleteDownloadDetailFromDB() = runBlocking {
        addOneDownloadDetailToDB()
        downloadDetailDao.deleteDownloadDetail(downloadDetail1.downloadUrl)
        val actual = downloadDetailDao.getDownloadDetails(download.downloadId).first()
        Assert.assertEquals(0, actual.size)
    }

    @Test
    fun daoUpdateDownloadDetail_updateFileSize() = runBlocking {
        addOneDownloadDetailToDB()
        downloadDetailDao.updateDownloadDetail(downloadDetail1.copy(fileSize = 11451))
        val actual = downloadDetailDao.getDownloadDetail(downloadDetail1.downloadUrl).first()
        Assert.assertNotEquals(downloadDetail1.fileSize, actual.fileSize)
    }

    @Test
    fun daoGetNotExistsDownloadDetails() = runBlocking {
        val actual = downloadDetailDao.getDownloadDetails(1515).first()
        Assert.assertTrue(actual.isEmpty())
    }

    private suspend fun addOneDownloadToDB() {
        downloadDao.insertDownload(download)
    }

    private suspend fun addOneDownloadDetailToDB() {
        addOneDownloadToDB()
        downloadDetailDao.insertDownloadDetail(downloadDetail1)
    }

    private suspend fun addTwoDownloadDetailToDB() {
        addOneDownloadToDB()
        downloadDetailDao.insertDownloadDetail(downloadDetail1)
        downloadDetailDao.insertDownloadDetail(downloadDetail2)
    }

}