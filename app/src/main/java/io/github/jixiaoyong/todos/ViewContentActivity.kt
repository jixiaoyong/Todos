package io.github.jixiaoyong.todos

import android.os.Bundle
import io.github.jixiaoyong.todos.bean.ContentBean
import kotlinx.android.synthetic.main.activity_content_view.*
import java.text.SimpleDateFormat
import android.webkit.WebView
import android.webkit.WebViewClient



/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/10/1
 * description: todo
 */
class ViewContentActivity : BaseActivity() {

    private lateinit var content:ContentBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_view)

        content = ContentBean(intent.getLongExtra("date",0),
                intent.getLongExtra("userId",0),
                intent.getStringExtra("tag"),
                intent.getIntExtra("state",0),
                intent.getLongExtra("contentId",0),
                intent.getStringExtra("title"),
                intent.getStringExtra("content"),
                intent.getStringExtra("url"),
                intent.getStringExtra("username")
        )

        initView()

        web_view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }

        if (!content.url.isNullOrEmpty()) {
            web_view.loadUrl(content.url)
        }
    }

    private fun initView() {
        content_title.text = content.title
        author.text = content.username
        tag.text = content.tag
        time.text = SimpleDateFormat("YYYY-MM-dd HH:mm").format(content.date * 1000)
        state.text = when {
            content.state == 0 -> getString(R.string.state_private)
            content.state == 1 -> getString(R.string.state_check)
            else -> getString(R.string.state_public)
        }
    }
}