package io.github.jixiaoyong.todos.bean

import com.google.gson.annotations.SerializedName

data class ResultContentQuery(@SerializedName("code")
                    val code: Int = 0,
                              @SerializedName("data")
                    val data: List<ContentBean>?,
                              @SerializedName("message")
                    val message: String = "")