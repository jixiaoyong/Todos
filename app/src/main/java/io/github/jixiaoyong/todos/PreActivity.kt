package io.github.jixiaoyong.todos

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_content_view.*

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/10/1
 * description: todo
 */
class PreActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_view)

        web_view.loadUrl("https://ramen.gq")
    }


}