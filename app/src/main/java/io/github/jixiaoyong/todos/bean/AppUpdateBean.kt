package io.github.jixiaoyong.todos.bean


import com.google.gson.annotations.SerializedName

data class App(@SerializedName("app_name")
               val appName: String = "",
               @SerializedName("app_update_info")
               val appUpdateInfo: String = "",
               @SerializedName("app_url")
               val appUrl: String = "",
               @SerializedName("app_version_code")
               val appVersionCode: Int = 0,
               @SerializedName("is_update")
               val isUpdate: Boolean = false,
               @SerializedName("app_version_name")
               val appVersionName: String = "")


data class AppUpdateInfo(@SerializedName("app")
                val app: App? = null,
                @SerializedName("is_update")
                val isUpdate: Boolean = false)


data class AppUpdateBean(@SerializedName("code")
                         val code: Int = 0,
                         @SerializedName("data")
                         val data: AppUpdateInfo,
                         @SerializedName("message")
                         val message: String = "")


