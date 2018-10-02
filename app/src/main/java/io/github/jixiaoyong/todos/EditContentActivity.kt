package io.github.jixiaoyong.todos

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.widget.EditText
import android.widget.LinearLayout
import io.github.jixiaoyong.todos.bean.ResultContentAdd
import io.github.jixiaoyong.todos.widget.BaseDialog
import io.github.jixiaoyong.todos.widget.LogUtils
import io.github.jixiaoyong.todos.widget.Toast
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_content_edit.*

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/10/1
 * description: todo
 */
class EditContentActivity : BaseActivity() {

    private lateinit var mDialog: BaseDialog
    private lateinit var mContext: Context
    private lateinit var mTodosService: TodosService
    private lateinit var mToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_edit)

        mContext = this

        mToken = intent.getStringExtra("token")

        mDialog = BaseDialog(this)
        mTodosService = RetrofitManager.retrofit.create(TodosService::class.java)

        bindEvent()

    }

    private fun bindEvent() {

        add_tag.setOnClickListener {
            var layout = LinearLayout(this)
            layout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)

            var editText = EditText(this)
            editText.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            editText.setSingleLine()
            editText.filters = arrayOf(InputFilter.LengthFilter(10))

            layout.addView(editText)
            mDialog.setContentView(layout)
            mDialog.setTitle(getString(R.string.add_tag))
            mDialog.setButtonClickListener(object : () -> Unit {
                override fun invoke() {
                    if (editText.text.isNotEmpty()) {
                        tag.text = editText.text
                    }
                    mDialog.dismiss()
                }
            },
                    object : () -> Unit {
                        override fun invoke() {
                            mDialog.dismiss()
                        }
                    })
            mDialog.show()
        }

        fab.setOnClickListener {
            if (content_title.text.isNullOrEmpty()) {
                Toast.show(getString(R.string.content_cant_empty))
                return@setOnClickListener
            }
            if (content_title.text.isNullOrEmpty()) {
                Toast.show(getString(R.string.title_cant_empty))
                return@setOnClickListener
            }

            if (!url.text.isNullOrEmpty()) {
                if (!url.text.startsWith("http://", true)
                        && !url.text.startsWith("https://", true)) {
                    Toast.show(getString(R.string.url_must_start_http_https))
                    return@setOnClickListener
                }
            }

            updateContent()
        }
    }

    private fun updateContent() {
        mTodosService.contentAdd(mToken,
                content_title.text.toString(),
                content.text.toString(),
                url.text.toString(),
                tag.text.toString(),
                0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResultContentAdd> {
                    override fun onComplete() {
                        LogUtils.d("on complete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        LogUtils.d("on subscribe")
                    }

                    override fun onNext(t: ResultContentAdd) {
                        if (t.code == 200) {
                            Toast.show(getString(R.string.save_success))
                            finish()
                        } else {
                            Toast.show(getString(R.string.save_failed))
                        }
                    }

                    override fun onError(e: Throwable) {
                        Toast.show(getString(R.string.save_failed))
                    }

                })
    }


}