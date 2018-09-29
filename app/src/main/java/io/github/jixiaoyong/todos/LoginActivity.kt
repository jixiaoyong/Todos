package io.github.jixiaoyong.todos

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.github.jixiaoyong.todos.greendao.DaoSession
import io.github.jixiaoyong.todos.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/29
 * description: todo
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var mDaoSession: DaoSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDaoSession = (application as MyApplication).mDaoSession
        val userQuery = mDaoSession.userDao.queryBuilder().build()
        //todo check the token
//        if (userQuery.list().isNotEmpty()&&userQuery.list()[0].token.isNotEmpty()) {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }

        setContentView(R.layout.activity_login)

        bindEvent()

    }

    private fun bindEvent() {
        login_btn.setOnClickListener {
            Toast.show("Login")
        }

        register_btn.setOnClickListener {
            Toast.show("Register")
        }

        arrow_ibtn.setOnClickListener {
            var boo = confirm_password_layout.visibility == View.VISIBLE
            login_btn.isEnabled = boo
            register_btn.isEnabled = !boo
            confirm_password_layout.visibility = if (boo) View.GONE else View.VISIBLE
            arrow_ibtn.setBackgroundResource(if (boo) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up)
        }
    }

}