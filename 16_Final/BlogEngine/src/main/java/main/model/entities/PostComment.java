package main.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post_comments")
public class PostComment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", nullable = true)
    private PostComment parent;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private Timestamp time;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<PostComment> childPostComments = new HashSet<PostComment>();

    public PostComment(PostComment parentComment, User user, Post parentPost, Timestamp time, String text) {
        this.parent = parentComment;
        this.user = user;
        this.post = parentPost;
        this.time = time;
        this.text = text;
    }

    public PostComment() {}

    // Геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PostComment getParent() {
        return parent;
    }

    public void setParent(PostComment parent) {
        this.parent = parent;
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

    public Set<PostComment> getChildPostComments() {
        return childPostComments;
    }

    public void setChildPostComments(Set<PostComment> childPostComments) {
        this.childPostComments = childPostComments;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}