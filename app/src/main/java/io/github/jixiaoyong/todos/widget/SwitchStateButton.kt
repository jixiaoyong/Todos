package io.github.jixiaoyong.todos.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import java.util.jar.Attributes

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/29
 * description: todo
 */
class SwitchStateButton(context: Context,attrs: AttributeSet?,defStyleAttr:Int,defStyleRes:Int)
    :Button(context,attrs,defStyleAttr,defStyleRes) {

    constructor(context: Context,attrs: AttributeSet?,defStyleAttr:Int):
            this(context, attrs, defStyleAttr,0)
    constructor(context: Context,attrs: AttributeSet?):
            this(context, attrs, 0,0)
    constructor(context: Context):
            this(context, null, 0,0)

    init {

    }

}