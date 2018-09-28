package io.github.jixiaoyong.todos.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author: jixiaoyong
 * email: jixiaoyong1995@gmail.com
 * website: www.jixiaoyong.github.io
 * date: 2018/9/28
 * description: todo
 */
@Entity(indexes = {
        @Index(value = "id", unique = true)
})
public class Content {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private long conetnt_id;
    private long user_id;
    private String username;
    private String title;
    private String content;
    private String tag;
    private String url;
    private int state;
    private int date;

    public Content(long conetnt_id, long user_id, String username,
                   String title, String content, String tag, String url, int state,
                   int date) {
        this.conetnt_id = conetnt_id;
        this.user_id = user_id;
        this.username = username;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.url = url;
        this.state = state;
        this.date = date;
    }

@Generated(hash = 732698304)
public Content(Long id, long conetnt_id, long user_id, String username,
        String title, String content, String tag, String url, int state,
        int date) {
    this.id = id;
    this.conetnt_id = conetnt_id;
    this.user_id = user_id;
    this.username = username;
    this.title = title;
    this.content = content;
    this.tag = tag;
    this.url = url;
    this.state = state;
    this.date = date;
}

@Generated(hash = 940998559)
public Content() {
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public long getConetnt_id() {
    return this.conetnt_id;
}

public void setConetnt_id(long conetnt_id) {
    this.conetnt_id = conetnt_id;
}

public long getUser_id() {
    return this.user_id;
}

public void setUser_id(long user_id) {
    this.user_id = user_id;
}

public String getUsername() {
    return this.username;
}

public void setUsername(String username) {
    this.username = username;
}

public String getTitle() {
    return this.title;
}

public void setTitle(String title) {
    this.title = title;
}

public String getContent() {
    return this.content;
}

public void setContent(String content) {
    this.content = content;
}

public String getTag() {
    return this.tag;
}

public void setTag(String tag) {
    this.tag = tag;
}

public String getUrl() {
    return this.url;
}

public void setUrl(String url) {
    this.url = url;
}

public int getState() {
    return this.state;
}

public void setState(int state) {
    this.state = state;
}

public int getDate() {
    return this.date;
}

public void setDate(int date) {
    this.date = date;
}
}
