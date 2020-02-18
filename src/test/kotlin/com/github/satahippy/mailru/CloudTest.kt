package com.github.satahippy.mailru

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class CloudTest {

    private lateinit var cloud: Cloud

    @Before
    fun init() {
        cloud = Cloud.instance()
        cloud.login(Settings.username(), Settings.password())
    }

    @Test
    fun loginSuccess() {
        cloud = Cloud.instance()
        cloud.login(Settings.username(), Settings.password())
    }

    @Test(expected = MailruException::class)
    fun loginFailed() {
        cloud = Cloud.instance()
        cloud.login("some@mail.ru", "aaa")
    }

    @Test
    fun getFolder() {
        val result = cloud.getFolder("/").execute().body()
        assertNotNull(result)
        assertNotNull(result!!.body)
    }

    @Test
    fun addFolder() {
        val resultAdd = cloud.addFolder("/test_folder").execute().body()
        assertNotNull(resultAdd)
        assertEquals("/test_folder", resultAdd!!.body)

        val resultRemove = cloud.removeFile("/test_folder").execute().body()
        assertNotNull(resultRemove)
        assertEquals("/test_folder", resultRemove!!.body)
    }

    @Test
    fun uploadFile() {
        val resultUpload = cloud.uploadFile("/test_file", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".toByteArray()).execute().body()
        assertNotNull(resultUpload)
        assertEquals("/test_file", resultUpload!!.body)

        val resultRemove = cloud.removeFile("/test_file").execute().body()
        assertNotNull(resultRemove)
        assertEquals("/test_file", resultRemove!!.body)
    }

    @Test
    fun uploadSmallFile() {
        val resultUpload = cloud.uploadFile("/test_small_file", "aaa".toByteArray()).execute().body()
        assertNotNull(resultUpload)
        assertEquals("/test_small_file", resultUpload!!.body)

        val resultRemove = cloud.removeFile("/test_small_file").execute().body()
        assertNotNull(resultRemove)
        assertEquals("/test_small_file", resultRemove!!.body)
    }
}
