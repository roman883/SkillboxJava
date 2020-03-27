package main.model.repositories;

import main.model.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getRecentPosts(int offset, int limit);

    @Query(value = "SELECT p.* FROM posts AS p " +
            "JOIN (SELECT post_id, SUM(value) AS sum_values " +
            "FROM post_votes GROUP BY post_id) AS sum_votes " +
            "ON p.id=sum_votes.post_id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY sum_values DESC " +
            "LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getBestPosts(int offset, int limit);

    // SELECT p.* FROM posts AS p JOIN (SELECT post_id, SUM(value) AS sum_values FROM post_votes GROUP BY post_id) AS sum_votes ON p.id=sum_votes.post_id ORDER BY sum_values DESC

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.view_count DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getPopularPosts(int offset, int limit);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time ASC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getEarlyPosts(int offset, int limit);

    @Query(value = "SELECT count(id) AS count FROM posts", nativeQuery = true)
    int countAllPosts();

    @Query(value = "SELECT * FROM posts p " +
            "WHERE (p.text LIKE %?3% OR p.title LIKE %?3%) " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> searchPosts(int offset, int limit, String query);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE DATEDIFF(p.time, ?) = 0 " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ? OFFSET ?", nativeQuery = true)
    List<Post> getPostsByDate(String date, int limit, int offset);

    @Query(value = "SELECT p.* " +
            "FROM posts AS p " +
            "INNER JOIN tag2post t2 ON p.id = t2.post_id " +
            "INNER JOIN tags t ON t.id  = t2.tag_id " +
            "WHERE (t.name LIKE %?1%) " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC " +
            "LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Post> getPostsByTag(String tag, int limit, int offset);


    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'NEW' " +
            "ORDER BY p.time DESC LIMIT ? OFFSET ?", nativeQuery = true)
    List<Post> getPostsForModeration(int limit, int offset);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.moderator_id = ?4 " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = ?1 " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Post> getPostsModeratedByMe(String status, int limit, int offset, int moderatorId);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.user_id = ?4 " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = ?1 " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Post> getMyActivePosts(String status, int limit, int offset, int userId);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.user_id = ?1 " +
            "AND p.is_active = 0 " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Post> getMyNotActivePosts(int userId, int limit, int offset);

    @Query(value = "SELECT * FROM posts p  " +
            "WHERE YEAR(p.time) = ?", nativeQuery = true)
    List<Post> getPostsByYear(int year);

    // или такой запрос: SELECT YEAR(p.time) AS post_year FROM posts p GROUP BY post_year ORDER BY post_year DESC
    @Query(value = "SELECT DISTINCT YEAR(p.time) AS post_year " +
            "FROM posts p ORDER BY post_year DESC", nativeQuery = true)
    List<Integer> countYearsWithAnyPosts();

    @Query(value = "SELECT count(p.id) AS count " +
            "FROM post_votes p WHERE p.value = 1", nativeQuery = true)
    int countAllLikes();

    @Query(value = "SELECT count(p.id) AS count " +
            "FROM post_votes p WHERE p.value = -1", nativeQuery = true)
    int countAllDislikes();

    @Query(value = "SELECT sum(p.view_count) AS views FROM posts p", nativeQuery = true)
    int countAllViews();

    @Query(value = "SELECT p.time FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "ORDER BY p.time ASC LIMIT 1", nativeQuery = true)
    Timestamp getFirstPublicationDate();
}