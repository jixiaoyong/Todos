package io.github.jixiaoyong.todos.bean


import com.google.gson.annotations.SerializedName

data class ResultContentAdd(@SerializedName("code")
                            val code: Int = 0,
                            @SerializedName("data")
                            val data: ContentAddResult,
                            @SerializedName("message")
                            val message: String = "")


data class ContentAddResult(@SerializedName("content_id")
                val contentId: Int = 0)


