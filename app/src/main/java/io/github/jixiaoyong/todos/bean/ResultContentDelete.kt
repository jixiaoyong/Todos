package io.github.jixiaoyong.todos.bean


import com.google.gson.annotations.SerializedName

data class ResultContentDelete(@SerializedName("code")
                               val code: Int = 0,
                               @SerializedName("message")
                               val message: String = "")


