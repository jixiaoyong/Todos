package io.github.jixiaoyong.todos.bean

import com.google.gson.annotations.SerializedName

data class ContentBean(@SerializedName("date")
                       var date: Long = 0,
                       @SerializedName("user_id")
                       var userId: Long = 0,
                       @SerializedName("tag")
                       var tag: String? = null,
                       @SerializedName("state")
                       var state: Int = 0,
                       @SerializedName("conetnt_id")
                       var conetntId: Long = 0,
                       @SerializedName("title")
                       var title: String = "",
                       @SerializedName("content")
                       var content: String = "",
                       @SerializedName("url")
                       var url: String? = null,
                       @SerializedName("username")
                       var username: String = "")