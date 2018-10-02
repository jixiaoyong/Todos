package io.github.jixiaoyong.todos

import io.github.jixiaoyong.todos.bean.*
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/27
 * description: todo
 */
interface TodosService {

//    @FormUrlEncoded
//    @POST("user/login")
//    fun login(@Field("username") username: String, @Field("password") password: String)
//            : Call<ResultUserLogin>

    @FormUrlEncoded
    @POST("user/login")
    fun login(@Field("username") username: String,
              @Field("password") password: String)
            : Observable<ResultUserLogin>

    @FormUrlEncoded
    @POST("user/register")
    fun register(@Field("username") username: String,
                 @Field("password") password: String,
                 @Field("email") email: String?): Observable<ResultUserRegister>

    @FormUrlEncoded
    @POST("content/query")
    fun queryAllByToken(@Field("token") token: String): Observable<HttpResult<ContentBean>>

    @FormUrlEncoded
    @POST("content/query")
    fun queryPublic(@Field("token") token: String,
                    @Field("state") state: Int)
            : Observable<HttpResult<ContentBean>>

    @FormUrlEncoded
    @POST("content/add")
    fun contentAdd(@Field("token") token: String,
                   @Field("title") title: String,
                   @Field("content") content: String,
                   @Field("url") url: String?,
                   @Field("tag") tag: String?,
                   @Field("state") state: Int?): Observable<ResultContentAdd>

    @FormUrlEncoded
    @POST("content/delete")
    fun contentDelete(@Field("token") token: String,
                      @Field("content_id") contentId: Int): Observable<ResultContentDelete>

    @FormUrlEncoded
    @POST("app/update")
    fun appUpdate(@Field("current_version_code") current_version_code: Int): Observable<ResultContentDelete>
}