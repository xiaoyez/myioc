package test.entity;

import com.ioc.context.annotation.Autowired;
import com.ioc.context.annotation.Component;

@Component
public class Lost {
    private Integer id;

    @Autowired(value = "user")
    private User user;

    public Lost() {
    }

    public Lost(Integer id, User user) {
        this.id = id;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Lost{" +
                "id=" + id +
                ", user=" + user +
                '}';
    }
}
