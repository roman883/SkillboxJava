package main.model;

import javax.persistence.*;

@Entity
@Table(name = "tag2post")
public class TagToPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}

// id INT NOT NULL AUTO_INCREMENT
//● post_id INT NOT NULL - пост
//● tag_id INT NOT NULL - тэг