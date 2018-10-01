package io.github.jixiaoyong.todos.bean

import com.google.gson.annotations.SerializedName

data class Data(@SerializedName("date")
                val date: Long = 0,
                @SerializedName("email")
                val email: String? = null,
                @SerializedName("username")
                val username: String = "",
                @SerializedName("token")
                val token: String = "")