package com.github.satahippy.mailru

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CloudTest {

    private lateinit var cloud: Cloud

    private val preExistingFile = "/test_file"
    private val preExistingFileContent = "test_file_content"

    @Before
    fun init() {
        cloud = Cloud.instance()
        cloud.login(Settings.username(), Settings.password())
    }

    @Test
    fun login_Success() {
        cloud = Cloud.instance()
        cloud.login(Settings.username(), Settings.password())
    }

    @Test(expected = MailruException::class)
    fun login_Failed() {
        cloud = Cloud.instance()
        cloud.login("some@mail.ru", "aaa")
    }

    @Test
    fun getFolder_Existent() {
        val result = cloud.getFolder("/").execute().body()
        assertNotNull(result)
        assertNotNull(result!!.body)
        assertEquals(FileType.folder, result.body.type)
    }

    @Test
    fun getFolder_NonExistent() {
        val result = cloud.getFolder("/nonexistent_folder").execute().body()
        assertNull(result)
    }

    @Test
    fun getFile_Existent() {
        val result = cloud.getFile(preExistingFile).execute().body()
        assertNotNull(result)
        assertNotNull(result!!.body)
        assertEquals(FileType.file, result.body.type)
    }

    @Test
    fun getFile_NonExistent() {
        val result = cloud.getFile("/nonexistent_file").execute().body()
        assertNull(result)
    }

    @Test
    fun addFolder() {
        val resultAdd = cloud.addFolder("/test_folder_added").execute().body()
        assertNotNull(resultAdd)
        assertEquals("/test_folder_added", resultAdd!!.body)

        val resultRemove = cloud.removeFile("/test_folder_added").execute().body()
        assertNotNull(resultRemove)
        assertEquals("/test_folder_added", resultRemove!!.body)
    }

    @Test
    fun uploadFile() {
        val resultUpload = cloud.uploadFile("/test_file_added", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".toByteArray()).execute().body()
        assertNotNull(resultUpload)
        assertEquals("/test_file_added", resultUpload!!.body)

        val resultRemove = cloud.removeFile("/test_file_added").execute().body()
        assertNotNull(resultRemove)
        assertEquals("/test_file_added", resultRemove!!.body)
    }

    @Test
    fun uploadFile_Small() {
        val resultUpload = cloud.uploadFile("/test_small_file_added", "aaa".toByteArray()).execute().body()
        assertNotNull(resultUpload)
        assertEquals("/test_small_file_added", resultUpload!!.body)

        val resultRemove = cloud.removeFile("/test_small_file_added").execute().body()
        assertNotNull(resultRemove)
        assertEquals("/test_small_file_added", resultRemove!!.body)
    }

    @Test
    fun downloadFile_Existent() {
        val result = cloud.downloadFile(preExistingFile).execute().body()
        assertNotNull(result)
        assertEquals(preExistingFileContent, result!!.string())
    }

    @Test
    fun downloadFile_NonExistent() {
        val result = cloud.downloadFile("/nonexistent_file").execute().body()
        assertNull(result)
    }
}
