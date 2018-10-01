package io.github.jixiaoyong.todos.widget

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import io.github.jixiaoyong.todos.R
import kotlinx.android.synthetic.main.layout_base_dialog.view.*

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/29
 * description: todo
 */
class BaseDialog(context: Context) {

    protected var mDialog = AlertDialog.Builder(context).create()
    protected var mview: View = LayoutInflater.from(context).inflate(R.layout.layout_base_dialog, null, false)

    protected var mConfirmListener: (() -> Unit)? = null
    protected var mCancelListener: (() -> Unit)? = null

    init {

        mview.confirm_btn.setOnClickListener {
            mConfirmListener?.invoke()
        }

        mview.cancel_btn.setOnClickListener {
            mCancelListener?.invoke()
        }

        mDialog.setView(mview)
        mDialog.window.setBackgroundDrawableResource(R.drawable.bg_white_round)
        mDialog.setCancelable(false)

    }

    fun setButtonCount(count: Int): BaseDialog {
        when (count) {
            0 -> {
                mview.button_layout.visibility = View.GONE
            }
            1 -> {
                mview.button_layout.visibility = View.VISIBLE
                mview.confirm_btn.visibility = View.VISIBLE
                mview.cancel_btn.visibility = View.GONE
                mview.btn_divider.visibility = View.GONE
            }
            2 -> {
                mview.button_layout.visibility = View.VISIBLE
                mview.confirm_btn.visibility = View.VISIBLE
                mview.cancel_btn.visibility = View.VISIBLE
                mview.btn_divider.visibility = View.VISIBLE
            }

        }

        return this
    }

    fun setTitleVisiable(boo: Boolean): BaseDialog {
        mview.title.visibility = if (boo) View.VISIBLE else View.GONE
        return this
    }

    fun setContentView(view: View): BaseDialog {
        mview.content_view.removeAllViews()
        mview.content_view.addView(view)
        mDialog.setView(mview)
        return this
    }

    fun setButtonClickListener(confirmListener: (() -> Unit)?, cancelListener: (() -> Unit)?): BaseDialog {
        mConfirmListener = confirmListener
        mCancelListener = cancelListener
        return this
    }

    fun setButtonText(confirmButton: String, cancelButton: String): BaseDialog {
        mview.confirm_btn.text = confirmButton
        mview.cancel_btn.text = cancelButton
        return this
    }

    fun setTitle(title: String): BaseDialog {
        mview.title.text = title
        return this
    }

    fun setView(view: View): BaseDialog {
        mDialog.setView(view)
        return this
    }

    fun setCancelable(cancelable:Boolean): BaseDialog {
        mDialog.setCancelable(cancelable)
        return this
    }

    fun show() {
        if (!mDialog.isShowing) {
            mDialog.show()
        }
    }

    fun dismiss() {
        mDialog?.dismiss()
    }

}