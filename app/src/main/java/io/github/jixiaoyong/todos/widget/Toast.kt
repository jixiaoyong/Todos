package io.github.jixiaoyong.todos.widget

import android.content.Context
import android.widget.Toast

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/27
 * description: todo
 */

object Toast {

    val LENGTH_LONG = Toast.LENGTH_LONG
    val LENGTH_SHORT = Toast.LENGTH_SHORT

    private lateinit var mToast: Toast

    fun init(context: Context) {
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
    }

    fun show(text: String) {
        mToast.setText(text)
        mToast.show()
    }

    fun show(text: String, duration: Int) {
        mToast.setText(text)
        mToast.duration = duration
        mToast.show()
    }

}