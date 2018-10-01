package io.github.jixiaoyong.todos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
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
import kotlinx.android.synthetic.main.layout_item_content.view.*
import org.greenrobot.greendao.query.Query
import java.text.SimpleDateFormat

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/27
 * description: 主页面，分为个人文章列表、写文章页面、发现文章列表
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mTodosService: TodosService
    private lateinit var mContext: Context
    private lateinit var mToken: String
    private var mContentId = 0

    private var mShowContents = ArrayList<ContentBean>()
    private var mUserContents = ArrayList<ContentBean>()
    private var mPublicContents = ArrayList<ContentBean>()

    private lateinit var mDaoSession: DaoSession
    private lateinit var mContentDao: ContentDao
    private lateinit var mContentQuery: Query<Content>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this

        mToken = intent.getStringExtra("token")

        Log.d("TAG", "token is $mToken")

        mTodosService = RetrofitManager.retrofit.create(TodosService::class.java)

        mDaoSession = (application as MyApplication).mDaoSession
        mContentDao = mDaoSession.contentDao

        mContentQuery = mContentDao.queryBuilder().build()
        mContentQuery.list().map {
            mShowContents.add(ContentBean(it.date, it.user_id, it.tag, it.state, it.conetnt_id, it.title,
                    it.content, it.url, it.username))
        }

        initView()

        bindEvent()
    }

    private fun initView() {

        recycler_view.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        recycler_view.adapter = object : RecyclerView.Adapter<MViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MViewHolder {
                return MViewHolder(layoutInflater.inflate(R.layout.layout_item_content, p0, false))

            }

            override fun getItemCount(): Int {
                return mShowContents.size
            }

            override fun onBindViewHolder(p0: MViewHolder, p1: Int) {
                p0.itemView.title.text = mShowContents[p1].title
                p0.itemView.author.text = mShowContents[p1].username
                p0.itemView.content.text = mShowContents[p1].content
                p0.itemView.tags.text = mShowContents[p1].tag //todo 记得更改为复数
                p0.itemView.state.text = when {
                    mShowContents[p1].state == 0 -> getString(R.string.state_private)
                    mShowContents[p1].state == 1 -> getString(R.string.state_check)
                    else -> getString(R.string.state_public)
                }

                var simpleDataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                p0.itemView.time.text = simpleDataFormat.format(mShowContents[p1].date * 1000)
                p0.itemView.setOnClickListener {
                    var intent = Intent(mContext, ViewContentActivity::class.java)
                    intent.putExtra("title", mShowContents[p1].title)
                    intent.putExtra("username", mShowContents[p1].username)
                    intent.putExtra("content", mShowContents[p1].content)
                    intent.putExtra("date", mShowContents[p1].date)
                    intent.putExtra("tag", mShowContents[p1].tag)
                    intent.putExtra("state", mShowContents[p1].state)
                    intent.putExtra("url", mShowContents[p1].url)
                    intent.putExtra("userId", mShowContents[p1].userId)
                    intent.putExtra("contentId", mShowContents[p1].conetntId)
                    startActivity(intent)
                }
            }

        }
    }

    private fun bindEvent() {
        //todo 检测是否有更新

        if (navigation_view.selectedItemId == R.id.menu_index) {
            rxAndroidGo(mTodosService.queryAllByToken(mToken), MSG_CONTENT_QUERY_ALL)
        } else if (navigation_view.selectedItemId == R.id.menu_discover) {
            rxAndroidGo(mTodosService.queryPublic(mToken, 2), MSG_CONTENT_QUERY_PUBLIC)
        }

        swipe_refresh_layout.setOnRefreshListener {
            if (navigation_view.selectedItemId == R.id.menu_index) {
                rxAndroidGo(mTodosService.queryAllByToken(mToken), MSG_CONTENT_QUERY_ALL)
                Log.d("TAG", "query all")
            } else if (navigation_view.selectedItemId == R.id.menu_discover) {
                rxAndroidGo(mTodosService.queryPublic(mToken, 2), MSG_CONTENT_QUERY_PUBLIC)
                Log.d("TAG", "query public")
            }
        }

        navigation_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_index -> {
                    mPublicContents.clear()
                    mPublicContents.addAll(mShowContents)
                    mShowContents.clear()
                    mShowContents.addAll(mUserContents)
                    recycler_view.adapter?.notifyDataSetChanged()
                    if (mShowContents.isEmpty()) {
                        rxAndroidGo(mTodosService.queryAllByToken(mToken), MSG_CONTENT_QUERY_ALL)
                    }
                }
                R.id.menu_discover -> {
                    mUserContents.clear()
                    mUserContents.addAll(mShowContents)
                    mShowContents.clear()
                    mShowContents.addAll(mPublicContents)
                    recycler_view.adapter?.notifyDataSetChanged()
                    if (mPublicContents.isEmpty()) {
                        rxAndroidGo(mTodosService.queryPublic(mToken, 2), MSG_CONTENT_QUERY_PUBLIC)
                    }
                }
                R.id.menu_edit -> {
                    startActivity(Intent(mContext, EditContentActivity::class.java))
                }
            }

            Log.d("TAG", "click on menu_index ${it.itemId == R.id.menu_index}")
            Log.d("TAG", "click on menu_discover ${it.itemId == R.id.menu_discover}")
            return@setOnNavigationItemSelectedListener true
        }

//        rxAndroidGo(mTodosService.contentAdd(mToken, content_title.text.toString(),
//                content_content.text.toString(), "http://www.ramen.gq", "", null),
//                MSG_CONTENT_ADD)
//        rxAndroidGo(mTodosService.contentDelete(mToken, mContentId), MSG_CONTENT_DELETE)
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
                MSG_CONTENT_QUERY_ALL -> {
                    val t = msg.obj as HttpResult<ContentBean>
                    Log.d("TAG", "data is ${t.code} ${t.message} \n ${t.data.size}")
                    storeContents(t.data, mUserContents, null)
                }
                MSG_CONTENT_ADD -> {
                    val t = msg.obj as ResultContentAdd
//                    result_tv.text = "code:${t.code}\nmessage:${t.message}\n${t.data}"
                    Toast.show("success")
                    mContentId = t.data.contentId
                }
                MSG_CONTENT_DELETE -> {
                    val t = msg.obj as ResultContentDelete
//                    result_tv.text = "code:${t.code}\nmessage:${t.message}\n"
                }
                MSG_CONTENT_QUERY_PUBLIC -> {
                    val t = msg.obj as HttpResult<ContentBean>
                    Log.d("TAG", "data is ${t.code} ${t.message} \n ${t.data.size}")
                    storeContents(t.data, mPublicContents, 2)

                }
            }
        }
    }

    fun storeContents(data: Array<ContentBean>, mContents: ArrayList<ContentBean>, state: Int?) {
        if (data != null) {
            data?.map {
                mContentDao.insertOrReplace(Content(it.conetntId, it.userId,
                        it.username, it.title, it.content, it.tag,
                        it.url, it.state, it.date))

                mContentQuery = mContentDao.queryBuilder().build()
                mContentQuery.list().map {
                    when (state) {
                        null -> mContents.add(ContentBean(it.date, it.user_id, it.tag, it.state,
                                it.conetnt_id, it.title,
                                it.content, it.url, it.username))
                        it.state -> mContents.add(ContentBean(it.date, it.user_id, it.tag, it.state,
                                it.conetnt_id, it.title,
                                it.content, it.url, it.username))
                        else -> {

                        }
                    }
                }
            }
            mShowContents.clear()
            mShowContents.addAll(mContents)
            recycler_view.adapter?.notifyDataSetChanged()
            swipe_refresh_layout.isRefreshing = false
        }
    }
}
