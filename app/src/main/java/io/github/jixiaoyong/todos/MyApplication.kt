package io.github.jixiaoyong.todos

import android.app.Application
import io.github.jixiaoyong.todos.greendao.DaoMaster
import io.github.jixiaoyong.todos.greendao.DaoSession
import io.github.jixiaoyong.todos.widget.BaseDialog
import io.github.jixiaoyong.todos.widget.LogUtils
import io.github.jixiaoyong.todos.widget.Toast

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/27
 * description: todo
 */
class MyApplication : Application() {

    lateinit var mDaoSession: DaoSession

    override fun onCreate() {
        super.onCreate()

        Toast.init(this)
        LogUtils.init(this)

        //greenDao
        var mDaoHelp = DaoMaster.DevOpenHelper(this, "todos")
        var todosDatabase = mDaoHelp.writableDatabase
        mDaoSession = DaoMaster(todosDatabase).newSession()
    }
}