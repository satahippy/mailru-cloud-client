package com.github.satahippy.mailru

data class MailruResponse<T>(
        var email: String,
        var time: Long,
        var status: Int,
        var body: T
)

data class InternalLoginRequest(
        var Login: String,
        var Password: String,
        var act_token: String,
        var page: String,
        var Domain: String,
        var FromAccount: String,
        var new_auth_form: Int,
        var saveauth: Int,
        var lang: String
) : FormUrlEncodedRequest

data class Sort(
    var type: String,
    var order: String
)

enum class FileType {
    file,
    folder
}

data class FolderOrFile(
        var count: FolderCount?,
        var name: String,
        var home: String,
        var size: Long?,
        var kind: FileType,
        var type: FileType,
        var tree: String?,
        var mtime: Int?,
        var rev: Int?,
        var grev: Int?,
        var virus_scan: String?,
        var hash: String?,
        var list: List<FolderOrFile>?
)

data class FolderCount(
        var folders: Int,
        var files: Int
)
