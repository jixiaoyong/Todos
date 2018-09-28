package io.github.jixiaoyong.todos.widget

import android.content.Context
import android.util.Log
import io.github.jixiaoyong.todos.BuildConfig

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/27
 * description: todo
 */
object LogUtils {

    lateinit var TAG: String
    var allowD = true
    var allowE = true
    var allowW = true
    var allowI = true

    fun init(context: Context) {
        initDebug(context, BuildConfig.DEBUG)
    }

    private fun initDebug(context: Context, debug: Boolean) {
        TAG = context.packageName
        setAllow(debug)
    }

    private fun setAllow(debug: Boolean) {
        allowD = debug
        allowE = debug
        allowW = debug
        allowI = debug
    }

    fun d(text: String) {
        if (allowD) {
            Log.d(TAG, text)
        }
    }

    fun e(text: String) {
        if (allowE) {
            Log.e(TAG, text)
        }
    }


    fun e(text: String, e: Throwable) {
        if (allowE) {
            Log.e(TAG, text, e)
        }
    }

    fun i(text: String) {
        if (allowI) {
            Log.i(TAG, text)
        }
    }

    fun w(text: String) {
        if (allowW) {
            Log.w(TAG, text)
        }
    }
}