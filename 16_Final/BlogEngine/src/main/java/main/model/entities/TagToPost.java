package main.model.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tag2post")
public class TagToPost implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @ManyToOne() //Каскад убрал, чтобы пользователи без тегов существовали
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public TagToPost() {}

    public TagToPost(Tag tag, Post post) {
        this.tag = tag;
        this.post = post;
    }

    // Геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}