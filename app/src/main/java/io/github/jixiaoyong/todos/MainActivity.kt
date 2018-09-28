package io.github.jixiaoyong.todos

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.github.jixiaoyong.todos.bean.*
import io.github.jixiaoyong.todos.greendao.*
import io.github.jixiaoyong.todos.widget.LogUtils
import io.github.jixiaoyong.todos.widget.Toast
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_content_rv.view.*
import org.greenrobot.greendao.query.Query


/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/27
 * description: todo
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mTodosService: TodosService
    private lateinit var mContext: Context
    private var mToken = ""
    private var mContentId = 0

    private var mContents = ArrayList<DataItem>()

    private lateinit var mDaoSession: DaoSession
    private lateinit var mUserDao: UserDao
    private lateinit var mContentDao: ContentDao
    private lateinit var mContentQuery: Query<Content>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this

        mTodosService = RetrofitManager.retrofit.create(TodosService::class.java)

        mDaoSession = (application as MyApplication).mDaoSession
        mUserDao = mDaoSession.userDao
        mContentDao = mDaoSession.contentDao
//
        mContentQuery = mContentDao.queryBuilder().build()
        mContentQuery.list().map {
            mContents.add(DataItem(it.date, it.user_id, it.tag, it.state, it.conetnt_id, it.title,
                    it.content, it.url, it.username))
        }


        initView()

        bindEvent()
    }

    private fun initView() {

        result_rv.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        result_rv.adapter = object : RecyclerView.Adapter<MViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MViewHolder {
                return MViewHolder(layoutInflater.inflate(R.layout.item_content_rv, p0, false))

            }

            override fun getItemCount(): Int {
                return mContents.size
            }

            override fun onBindViewHolder(p0: MViewHolder, p1: Int) {
                p0.itemView.title.text = mContents[p1].title
                p0.itemView.content.text = mContents[p1].content
                p0.itemView.date.text = mContents[p1].date.toString()
            }

        }
    }

    private fun bindEvent() {

        login_btn.setOnClickListener {
            //            mTodosService.login(username.text.toString(), password.text.toString()).enqueue(
//                    object : Callback<ResultUserLogin> {
//                        override fun onFailure(call: Call<ResultUserLogin>, t: Throwable) {
//                            Toast.show("failure")
//                            Log.e("TAG", "response failure " + t.message)
//                        }
//
//                        override fun onResponse(call: Call<ResultUserLogin>, response: Response<ResultUserLogin>) {
//                            Toast.show("success")
//                            Log.d("TAG", "response success" + response.body()?.message
//                                    + response.body()?.code
//                            + response.body()?.data)
//
//                        }
//
//                    }
//            )

            mTodosService.login(username.text.toString(), password.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ResultUserLogin> {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: ResultUserLogin) {
                            result_tv.text = "code:${t.code}\nmessage:${t.message}\ndata:${t.data}"
                            LogUtils.d(result_tv.text.toString())
                            mToken = t.data.token

                            var user = User(t.data.username, t.data.date, t.data.token, t.data.email)
                            mUserDao.insert(user)
                            LogUtils.d("username is ${user.username}  id is ${user.id}")

                        }

                        override fun onError(e: Throwable) {
                            LogUtils.e("error", e)
                        }
                    }
                    )

        }

        register_btn.setOnClickListener {
            mTodosService.register(username.text.toString(), password.text.toString(), email.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ResultUserRegister> {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: ResultUserRegister) {
                            result_tv.text = "code:${t.code}\nmessage:${t.message}\ndata:${t.data}"
                            mToken = t.data.token
                        }

                        override fun onError(e: Throwable) {
                            LogUtils.e("error", e)
                        }
                    }
                    )
        }

        query_all_btn.setOnClickListener {
            mTodosService.queryAllByToken(mToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ResultContentQuery> {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: ResultContentQuery) {
                            result_tv.text = "code:${t.code}\nmessage:${t.message}\n"
//                            mContents = t.data as ArrayList<DataItem>
//                            result_rv.adapter?.notifyDataSetChanged()

                            if (t.data!=null) {
                                t.data.map {
                                    mContentDao.insert(Content( it.conetntId, it.userId,
                                            it.username, it.title, it.content, it.tag,
                                            it.url, it.state, it.date))

                                    mContentQuery = mContentDao.queryBuilder().build()
                                    mContentQuery.list().map {
                                        mContents.add(DataItem(it.date, it.user_id, it.tag, it.state,
                                                it.conetnt_id, it.title,
                                                it.content, it.url, it.username))
                                    }
                                    result_rv.adapter?.notifyDataSetChanged()

                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            LogUtils.e("error", e)
                        }
                    }
                    )
        }

        add_btn.setOnClickListener {
            mTodosService.contentAdd(mToken, content_title.text.toString(),
                    content_content.text.toString(), "http://www.ramen.gq", "", null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ResultContentAdd> {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: ResultContentAdd) {
                            result_tv.text = "code:${t.code}\nmessage:${t.message}\n${t.data}"
                            Toast.show("success")
                            mContentId = t.data.contentId
                        }

                        override fun onError(e: Throwable) {
                            LogUtils.e("error", e)
                        }
                    }
                    )
        }

        delete_btn.setOnClickListener {
            mTodosService.contentDelete(mToken, mContentId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ResultContentDelete> {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: ResultContentDelete) {
                            result_tv.text = "code:${t.code}\nmessage:${t.message}\n"
                        }

                        override fun onError(e: Throwable) {
                            LogUtils.e("error", e)
                        }
                    }
                    )
        }
    }

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
