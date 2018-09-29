package io.github.jixiaoyong.todos

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.github.jixiaoyong.todos.bean.*
import io.github.jixiaoyong.todos.greendao.*
import io.github.jixiaoyong.todos.widget.LogUtils
import io.github.jixiaoyong.todos.widget.Toast
import io.reactivex.Observable
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
class ApiActivity : AppCompatActivity() {


    private lateinit var mTodosService: TodosService
    private lateinit var mContext: Context
    private var mToken = ""
    private var mContentId = 0

    private var mContents = ArrayList<ContentBean>()

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
        mContentQuery = mContentDao.queryBuilder().build()
        mContentQuery.list().map {
            mContents.add(ContentBean(it.date, it.user_id, it.tag, it.state, it.conetnt_id, it.title,
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
            rxAndroidGo(mTodosService.login(username.text.toString(), password.text.toString()),
                    MSG_USER_LOGIN)
        }

        register_btn.setOnClickListener {
            rxAndroidGo(mTodosService.register(username.text.toString(), password.text.toString(),
                    email.text.toString()), MSG_USER_REGISTER)
        }

        query_all_btn.setOnClickListener {
            rxAndroidGo(mTodosService.queryAllByToken(mToken), MSG_CONTENT_QUERY_ALL)
        }

        add_btn.setOnClickListener {
            rxAndroidGo(mTodosService.contentAdd(mToken, content_title.text.toString(),
                    content_content.text.toString(), "http://www.ramen.gq", "", null),
                    MSG_CONTENT_ADD)
        }

        delete_btn.setOnClickListener {
            rxAndroidGo(mTodosService.contentDelete(mToken, mContentId), MSG_CONTENT_DELETE)
        }
    }

    fun <T> rxAndroidGo(observable: Observable<T>, what: Int) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<T> {
                    override fun onComplete() {
                        LogUtils.d("on complete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        LogUtils.d("on subscribe")
                    }

                    override fun onNext(t: T) {
                        val msg = mHandler.obtainMessage(what, t)
                        mHandler.sendMessage(msg)
                    }

                    override fun onError(e: Throwable) {
                        val msg = mHandler.obtainMessage(what, e)
                        mHandler.sendMessage(msg)
                        LogUtils.e("error", e)
                    }

                })
    }


    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val MSG_USER_LOGIN = 0x001
    private val MSG_USER_REGISTER = 0x002
    private val MSG_USER_LOGOUT = 0x003
    private val MSG_USER_DELETE = 0x004
    private val MSG_CONTENT_QUERY_ALL = 0x005
    private val MSG_CONTENT_QUERY_ONE = 0x006
    private val MSG_CONTENT_QUERY_PUBLIC = 0x007
    private val MSG_CONTENT_ADD = 0x008
    private val MSG_CONTENT_DELETE = 0x009
    private val MSG_CONTENT_UPDATE = 0x010


    val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {

            if (msg?.obj is Throwable) {
                (msg.obj as Throwable).printStackTrace()
                return
            }

            when (msg?.what) {
                MSG_USER_LOGIN -> {
                    val t = msg.obj as ResultUserLogin
                    result_tv.text = "code:${t.code}\nmessage:${t.message}\ndata:${t.data}"
                    LogUtils.d(result_tv.text.toString())
                    mToken = t.data.token

                    val user = User(t.data.username, t.data.date, t.data.token, t.data.email)
                    mUserDao.insert(user)
                    LogUtils.d("username is ${user.username}  id is ${user.id}")
                }
                MSG_USER_REGISTER -> {
                    val t = msg.obj as ResultUserRegister
                    result_tv.text = "code:${t.code}\nmessage:${t.message}\ndata:${t.data}"
                    mToken = t.data.token
                }
                MSG_CONTENT_QUERY_ALL -> {
                    val t = msg.obj as HttpResult<ContentBean>
                    result_tv.text = "code:${t.code}\nmessage:${t.message}\n"

                    if (t.data != null) {
                        t.data?.map {
                            mContentDao.insertOrReplace(Content(it.conetntId, it.userId,
                                    it.username, it.title, it.content, it.tag,
                                    it.url, it.state, it.date))

                            mContentQuery = mContentDao.queryBuilder().build()
                            mContentQuery.list().map {
                                mContents.add(ContentBean(it.date, it.user_id, it.tag, it.state,
                                        it.conetnt_id, it.title,
                                        it.content, it.url, it.username))
                            }
                            result_rv.adapter?.notifyDataSetChanged()
                        }
                    }
                }
                MSG_CONTENT_ADD -> {
                    val t = msg.obj as ResultContentAdd
                    result_tv.text = "code:${t.code}\nmessage:${t.message}\n${t.data}"
                    Toast.show("success")
                    mContentId = t.data.contentId
                }
                MSG_CONTENT_DELETE -> {
                    val t = msg.obj as ResultContentDelete
                    result_tv.text = "code:${t.code}\nmessage:${t.message}\n"
                }

            }
        }
    }
}
