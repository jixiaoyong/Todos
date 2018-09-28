package io.github.jixiaoyong.todos.bean

import com.google.gson.annotations.SerializedName

data class DataItem(@SerializedName("date")
                    val date: Int = 0,
                    @SerializedName("user_id")
                    val userId: Long = 0,
                    @SerializedName("tag")
                    val tag: String? = null,
                    @SerializedName("state")
                    val state: Int = 0,
                    @SerializedName("conetnt_id")
                    val conetntId: Long = 0,
                    @SerializedName("title")
                    val title: String = "",
                    @SerializedName("content")
                    val content: String = "",
                    @SerializedName("url")
                    val url: String? = null,
                    @SerializedName("username")
                    val username: String = "")