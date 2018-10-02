package io.github.jixiaoyong.todos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import io.github.jixiaoyong.todos.bean.ResultUserLogin
import io.github.jixiaoyong.todos.bean.ResultUserRegister
import io.github.jixiaoyong.todos.greendao.DaoSession
import io.github.jixiaoyong.todos.greendao.User
import io.github.jixiaoyong.todos.greendao.UserDao
import io.github.jixiaoyong.todos.widget.BaseDialog
import io.github.jixiaoyong.todos.widget.LogUtils
import io.github.jixiaoyong.todos.widget.Toast
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/29
 * description: 鉴权（登录/注册）页面
 */
class LoginActivity : BaseActivity() {

    private lateinit var mDaoSession: DaoSession
    private lateinit var mUserDao: UserDao
    private lateinit var mContext: Context
    private lateinit var mDialog: BaseDialog

    private lateinit var mTodosService: TodosService

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        mContext = this

        mDialog = BaseDialog(this)

        mDaoSession = (application as MyApplication).mDaoSession
        mUserDao = mDaoSession.userDao
        val userQuery = mDaoSession.userDao.queryBuilder().build()

        //todo check the token
        if (userQuery.list().isNotEmpty() && userQuery.list()[0].token.isNotEmpty()) {
            go2MainActivity(userQuery.list()[0].token)
        }

        mTodosService = RetrofitManager.retrofit.create(TodosService::class.java)


        setContentView(R.layout.activity_login)

        bindEvent()

    }

    private fun bindEvent() {

        password.addTextChangedListener(mTextWatcher)
        confirm_password.addTextChangedListener(mTextWatcher)
        email.addTextChangedListener(mTextWatcher)

        login_btn.setOnClickListener {

            if (!checkUserNamePassword()) {
                Toast.show(getString(R.string.please_checkout_the_username_password))
                return@setOnClickListener
            }

            mDialog.setButtonCount(0)
                    .setContentView(layoutInflater.inflate(R.layout.layout_dialog_progress, null))
                    .setTitle(getString(R.string.login))
                    .show()

            rxAndroidGo(mTodosService.login(username.text.toString(), password.text.toString()), MSG_USER_LOGIN)

        }

        register_btn.setOnClickListener {
            if (!checkUserNamePassword()) {
                Toast.show(getString(R.string.please_checkout_the_username_password))
                return@setOnClickListener
            }

            mDialog.setButtonCount(0)
                    .setContentView(layoutInflater.inflate(R.layout.layout_dialog_progress, null))
                    .setTitle(getString(R.string.register))
                    .show()

            rxAndroidGo(mTodosService.register(username.text.toString(), password.text.toString(),
                    email.text.toString()), MSG_USER_REGISTER)
        }

        arrow_ibtn.setOnClickListener {
            var boo = register_more_layout.visibility == View.VISIBLE
            login_btn.isEnabled = boo
            register_btn.isEnabled = !boo
            register_more_layout.visibility = if (boo) View.GONE else View.VISIBLE
            arrow_ibtn.setBackgroundResource(if (boo) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up)
        }
    }

    fun checkUserNamePassword(): Boolean {

        var result = true

        if (username.text.isNullOrEmpty()) {
            username_layout.error = getString(R.string.username_cant_empty)
            result = false && result
        } else {
            username_layout.error = null
            result = true && result
        }

        var regex = Regex("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+\$")
        if (!email.text.isNullOrEmpty() && !regex.matches(email.text.toString())) {
            email_layout.error = getString(R.string.email_format_error)
            result = false && result
        } else {
            email_layout.error = null
            result = true && result
        }

        result = result && checkPassword(password, password_layout)

        //如果是注册的时候，才检查这些信息
        if (register_more_layout.visibility == View.VISIBLE) {

            result = result && checkPassword(confirm_password, confirm_password_layout)

            if (!confirm_password.text.toString().equals(password.text.toString())) {
                confirm_password_layout.error = getString(R.string.password_is_different)
                result = false && result
            } else {
                confirm_password_layout.error = null
                result = true && result
            }
        }

        return result
    }

    private val MSG_USER_LOGIN = 0x001
    private val MSG_USER_REGISTER = 0x002
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {

            if (msg?.obj is Throwable) {
                (msg.obj as Throwable).printStackTrace()
                mDialog.dismiss()
                Toast.show("error")
                return
            }

            when (msg?.what) {
                MSG_USER_LOGIN -> {
                    mDialog.dismiss()

                    val t = msg.obj as ResultUserLogin
                    val user = User(t.data.username, t.data.date, t.data.token, t.data.email)
                    mUserDao.deleteAll()
                    mUserDao.insert(user)

                    go2MainActivity(t.data.token)

                }
                MSG_USER_REGISTER -> {
                    mDialog.dismiss()
                    val t = msg.obj as ResultUserRegister
                    val user = User(t.data.username, t.data.date, t.data.token, t.data.email)
                    mUserDao.deleteAll()
                    mUserDao.insert(user)

                    go2MainActivity(t.data.token)
                }
            }
        }
    }

    private fun go2MainActivity(token: String) {
        var intent = Intent(mContext, MainActivity::class.java)
        intent.putExtra("token", token)
//        intent.putExtra("user_id", userId)
        startActivity(intent)
        finish()
    }

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            checkUserNamePassword()
        }

    }

    private fun checkPassword(password: TextInputEditText, password_layout: TextInputLayout): Boolean {

        if (password.text.isNullOrEmpty()) {
            password_layout.error = getString(R.string.password_cant_empty)
        } else if (password.text!!.length < 6) {
            password_layout.error = getString(R.string.password_too_short)
        } else if (password.text!!.length > 30) {
            password_layout.error = getString(R.string.password_too_long)
        } else {
            password_layout.error = null
            return true
        }

        return false
    }

    private fun <T> rxAndroidGo(observable: Observable<T>, what: Int) {
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

}