package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

//    @Column(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

//    @Column(name = "post_id")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private Date time;

    @Column(nullable = false, columnDefinition="TINYINT(1)")  // скрыта или активна публикация: 0 или 1
    private byte value;     //  value TINYINT NOT NULL - лайк или дизлайк: 1 или -1
}