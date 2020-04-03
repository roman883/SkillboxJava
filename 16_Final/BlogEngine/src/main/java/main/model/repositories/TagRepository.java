package main.model.repositories;

import main.model.entities.Tag;
import main.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query(value = "SELECT * FROM tags t" +
            " WHERE t.name = ?", nativeQuery = true)
    Tag getTagByTagName(String currentTagName);

//    @Query(value = "SELECT t.*, " +
//            "(sum_tags.counts / (SELECT MAX(tag_count) FROM (SELECT COUNT(post_id) AS tag_count " +
//            "FROM tag2post tp GROUP BY tp.tag_id ) AS max_tag_count)) as n_weight " +
//            "FROM tags t " +
//            "JOIN (SELECT tp.tag_id,  COUNT(post_id) AS counts FROM tag2post tp " +
//            "GROUP BY tp.tag_id) AS sum_tags " +
//            "ON t.id=sum_tags.tag_id " +
//            "ORDER BY counts DESC", nativeQuery = true, countName = )
//    Map<Tag, Integer> getTagsAndWeight();

    @Query(value = "SELECT * FROM tags t ORDER BY t.id DESC", nativeQuery = true)
    List<Tag> getAllTagsListSortedByIdDesc();

    @Query(value = "SELECT * FROM tags t " +
            "WHERE (t.name LIKE %?%) " +
            "ORDER BY t.id DESC", nativeQuery = true)
    List<Tag> getAllTagsListByQuerySortedByIdDesc(String query);

    @Query(value = "SELECT COUNT(*) " +
            "FROM tag2post tp " +
            "WHERE tag_id = ?", nativeQuery = true)
    Integer getTagCountByTagId(int tagId);

    @Query(value = "SELECT MAX(tag_count) " +
            "FROM (SELECT COUNT(post_id) AS tag_count " +
            "FROM tag2post tp GROUP BY tp.tag_id) " +
            "AS max_tag_count", nativeQuery = true)
    Integer getMaxTagCount();


}