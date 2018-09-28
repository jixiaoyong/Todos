package io.github.jixiaoyong.todos.bean

import com.google.gson.annotations.SerializedName

data class ResultUserLogin(@SerializedName("code")
                      val code: Int = 0,
                           @SerializedName("data")
                      val data: Data,
                           @SerializedName("message")
                      val message: String = "")