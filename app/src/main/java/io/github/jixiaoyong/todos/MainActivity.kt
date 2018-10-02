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
import android.widget.TextView
import io.github.jixiaoyong.todos.bean.*
import io.github.jixiaoyong.todos.greendao.*
import io.github.jixiaoyong.todos.widget.BaseDialog
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
import android.support.v4.content.ContextCompat.getSystemService
import android.app.DownloadManager
import android.net.Uri
import kotlinx.android.synthetic.main.activity_content_edit.*


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
    //    private var mUserId: Long = -0L
    private var mContentId = 0L

    private var mShowContents = ArrayList<ContentBean>()
    private var mUserContents = ArrayList<ContentBean>()
    private var mPublicContents = ArrayList<ContentBean>()

    private lateinit var mDialog: BaseDialog

    private lateinit var mDaoSession: DaoSession
    private lateinit var mContentDao: ContentDao
    private lateinit var mContentQuery: Query<Content>

    private var deleteId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        mDialog = BaseDialog(this)

        mToken = intent.getStringExtra("token")
//        mUserId = intent.getLongExtra("user_id",-0L)

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

        checkUpdate()
    }

    private fun checkUpdate() {

        rxAndroidGo(mTodosService.appUpdate(BuildConfig.VERSION_CODE), MSG_APP_UPDATE)
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
                if (navigation_view.selectedItemId == R.id.menu_index) {
                    p0.itemView.favorite.visibility = View.GONE
                    p0.itemView.delete.visibility = View.VISIBLE
                } else {
                    p0.itemView.favorite.visibility = View.VISIBLE
                    p0.itemView.delete.visibility = View.GONE
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

                p0.itemView.delete.setOnClickListener {
                    var textView = TextView(this@MainActivity)
                    textView.text = getString(R.string.delete_confirm_str)
                    mDialog.setContentView(textView)
                    mDialog.setTitle(getString(R.string.delete))
                    mDialog.setButtonClickListener(
                            object : () -> Unit {
                                override fun invoke() {
                                    deleteId = p1
                                    rxAndroidGo(mTodosService.contentDelete(mToken, mShowContents[p1].conetntId), MSG_CONTENT_DELETE)
                                    mDialog.dismiss()
                                }
                            },
                            object : () -> Unit {
                                override fun invoke() {
                                    mDialog.dismiss()
                                }
                            }
                    )
                    mDialog.show()
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
                    var intent = Intent(mContext, EditContentActivity::class.java)
                    intent.putExtra("token", mToken)
                    startActivity(intent)
                }
            }

            Log.d("TAG", "click on menu_index ${it.itemId == R.id.menu_index}")
            Log.d("TAG", "click on menu_discover ${it.itemId == R.id.menu_discover}")
            return@setOnNavigationItemSelectedListener true
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

    private val MSG_CONTENT_QUERY_ALL = 0x005
    private val MSG_CONTENT_QUERY_ONE = 0x006
    private val MSG_CONTENT_QUERY_PUBLIC = 0x007
    private val MSG_CONTENT_ADD = 0x008
    private val MSG_CONTENT_DELETE = 0x009
    private val MSG_CONTENT_UPDATE = 0x010
    private val MSG_APP_UPDATE = 0x011


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
                    checkResultCode(t.code)
                    storeContents(t.data, mUserContents, null)

                }
                MSG_CONTENT_ADD -> {
                    val t = msg.obj as ResultContentAdd
//                    result_tv.text = "code:${t.code}\nmessage:${t.message}\n${t.data}"
                    checkResultCode(t.code)
                    Toast.show("success")
                    mContentId = t.data.contentId
                }
                MSG_CONTENT_DELETE -> {
                    val t = msg.obj as ResultContentDelete
                    checkResultCode(t.code)
                    if (t.code == 200) {
                        Toast.show(getString(R.string.delete_success))
                        if (deleteId >= 0) {
                            mShowContents.removeAt(deleteId)
                            deleteId = -1
                        }
                        recycler_view.adapter?.notifyDataSetChanged()
                    } else {
                        Toast.show(getString(R.string.delete_failed))
                    }
                }
                MSG_CONTENT_QUERY_PUBLIC -> {
                    val t = msg.obj as HttpResult<ContentBean>
                    Log.d("TAG", "data is ${t.code} ${t.message} \n ${t.data.size}")
                    checkResultCode(t.code)
                    storeContents(t.data, mPublicContents, 2)
                }
                MSG_APP_UPDATE -> {
                    val t = msg.obj as AppUpdateBean
                    checkResultCode(t.code)
                    if (t.code == 200 && t.data.isUpdate) {
                        var textView = TextView(this@MainActivity)
                        var updateString = "${getString(R.string.version)} : ${t.data.app?.appVersionName}\n"+
                                "${getString(R.string.update_info)} : ${t.data.app?.appUpdateInfo}\n" +
                                getString(R.string.download_new_version)

                        textView.setText(updateString)
                        mDialog.setContentView(textView)
                        mDialog.setTitle(getString(R.string.new_version_coming))
                        mDialog.setButtonClickListener(
                                object : () -> Unit {
                                    override fun invoke() {
                                       downloadApk(t.data.app!!.appName,t.data.app!!.appUrl)
                                        mDialog.dismiss()
                                    }
                                },
                                object : () -> Unit {
                                    override fun invoke() {
                                        mDialog.dismiss()
                                    }
                                }
                        )
                        mDialog.show()
                    }
                }
            }
        }
    }

    private fun checkResultCode(code: Int) {
        if (code == 1005) {
            Toast.show(getString(R.string.please_login_agagin))
            mDaoSession.userDao.deleteAll()
            mDaoSession.contentDao.deleteAll()
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
        }
    }

    private fun downloadApk(appName:String,appUrl: String) {
        var mDownloadManager = applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        var uri = Uri.parse(appUrl)
        var request =  DownloadManager.Request(uri)
        request.setTitle(appName)
        request.setMimeType("application/vnd.android.package-archive")
        var downloadId = mDownloadManager.enqueue(request)
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
