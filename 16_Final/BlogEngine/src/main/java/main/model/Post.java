package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    // @Size(max = 1)
    //@Column(name = "    ", columnDefinition="TINYINT(3) UNSIGNED default '40'")
    @Column(name = "is_active", nullable = false, columnDefinition="TINYINT(1)")  // скрыта или активна публикация: 0 или 1
    private boolean isActive;

    // http://tomee.apache.org/examples-trunk/jpa-enumerated/
    @Column(name = "moderation_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus = ModerationStatus.NEW; // По умолчанию NEW

    @Column(name = "moderator_id", nullable = true)
    private Integer moderatorId = null;                 // ID пользователя-модератора, принявшего решение, или NULL

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;     // автор поста

    @Column(name = "time", nullable = false, columnDefinition = "DATETIME")
    private Date time;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    // Hibernate requires no-args constructor
}
