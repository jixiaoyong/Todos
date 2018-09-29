package io.github.jixiaoyong.todos.bean

import com.google.gson.annotations.SerializedName

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/29
 * description: todo
 */
data class HttpResult<T>(
        @SerializedName("code")
        val code: Int = 0,
        @SerializedName("data")
        val data: Array<T>,
        @SerializedName("message")
        val message: String = ""
)