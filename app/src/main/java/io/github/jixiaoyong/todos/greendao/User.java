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
        @Index(value = "username,id", unique = true)
})
public class User {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String username;
    private Long date;
    private String token;
    private String email;

    public User( @NotNull String username, Long date, String token,
                 String email) {
        this.username = username;
        this.date = date;
        this.token = token;
        this.email = email;
    }

@Generated(hash = 806324503)
public User(Long id, @NotNull String username, Long date, String token,
        String email) {
    this.id = id;
    this.username = username;
    this.date = date;
    this.token = token;
    this.email = email;
}

@Generated(hash = 586692638)
public User() {
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public String getUsername() {
    return this.username;
}

public void setUsername(String username) {
    this.username = username;
}

public Long getDate() {
    return this.date;
}

public void setDate(Long date) {
    this.date = date;
}

public String getToken() {
    return this.token;
}

public void setToken(String token) {
    this.token = token;
}

public String getEmail() {
    return this.email;
}

public void setEmail(String email) {
    this.email = email;
}

}
