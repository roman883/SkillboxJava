package main.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "post_votes")
public class PostVote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private Timestamp time;

    @Column(nullable = false)  // скрыта или активна публикация: 0 или 1 //columnDefinition="TINYINT(1)"
    private byte value;     //  value TINYINT NOT NULL - лайк или дизлайк: 1 или -1

    public PostVote() {}

    public PostVote(User user, Post post, Timestamp timestamp, byte value) {
        this.user = user;
        this.post = post;
        this.time = timestamp;
        this.value = value;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }
}