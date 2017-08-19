package com.github.satahippy.mailru

data class MailruResponse<T>(
        var email: String,
        var time: Long,
        var status: Int,
        var body: T
)

data class InternalLoginRequest(
        var page: String,
        var FailPage: String,
        var Domain: String,
        var Login: String,
        var Password: String,
        var new_auth_form: Int
) : FormUrlEncodedRequest

data class FolderOrFile(
        var count: FolderCount,
        var name: String,
        var home: String,
        var size: Long,
        var kind: String,
        var type: String,
        var tree: String,
        var mtime: Int,
        var rev: Int,
        var grev: Int,
        var virus_scan: String,
        var hash: String,
        var list: List<FolderOrFile>
)

data class FolderCount(
        var folders: Int,
        var files: Int
)