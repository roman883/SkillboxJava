package main.model.responses;

import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.TagToPost;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class ResponsePost implements ResponseAPI {

    private int id;
    private String time;
    private PostAuthorAPI user;
    private String title;
    private String text; //TODO Как-то конвертировать в HTML? Не String?
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<PostCommentAPI> comments;
    private List<String> tags;

    public ResponsePost(Post post) {
        id = post.getId();
        time = getTimeString(post.getTime().toLocalDateTime());
        user = new PostAuthorAPI(post.getUser().getId(), post.getUser().getName());
        title = post.getTitle();
        text = post.getText();
        likeCount = (int) post.getPostVotes().stream().filter(l -> l.getValue() == 1).count();
        dislikeCount = (int) post.getPostVotes().stream().filter(l -> l.getValue() == -1).count();
        viewCount = post.getViewCount();
        comments = new LinkedList<>();
        tags = new LinkedList<>();
        for (PostComment comment : post.getPostComments()) {
            int commentAuthorId = comment.getUser().getId();
            String commentAuthorName = comment.getUser().getName();
            String commentAuthorPhoto = comment.getUser().getPhoto();
            PostCommentAuthorAPI commentAuthor =
                    new PostCommentAuthorAPI(commentAuthorId, commentAuthorName, commentAuthorPhoto);
            int commentId = comment.getId();
            String commentTime = getTimeString(comment.getTime().toLocalDateTime());
            String commentText = comment.getText();
            PostCommentAPI postCommentAPI = new PostCommentAPI(commentId, commentTime, commentAuthor, commentText);
            comments.add(postCommentAPI);
        }
        for (TagToPost t : post.getTagsToPostsSet()) {
            String tagName = t.getTag().getName();
            if (!tags.contains(tagName)) {
                tags.add(tagName);
            }
        }
    }

    private String getTimeString(LocalDateTime objectCreatedTime) {
        StringBuilder timeString = new StringBuilder();
        if (objectCreatedTime.isAfter(LocalDateTime.now().minusDays(1))) {
            timeString.append("Сегодня, ").append(objectCreatedTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else if (objectCreatedTime.isAfter(LocalDateTime.now().minusDays(2))) {
            timeString.append("Вчера, ").append(objectCreatedTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            timeString.append(objectCreatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm")));
        }
        return timeString.toString();
    }

    // Конструктор, геттеры и сеттеры. Не используются (пока)
    public ResponsePost(int id, String time, PostAuthorAPI user, String title, String text, int likeCount,
                        int dislikeCount, int viewCount, List<PostCommentAPI> comments, List<String> tags) {
        this.id = id;
        this.time = time;
        this.user = user;
        this.title = title;
        this.text = text;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.viewCount = viewCount;
        this.comments = comments;
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public PostAuthorAPI getUser() {
        return user;
    }

    public void setUser(PostAuthorAPI user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public List<PostCommentAPI> getComments() {
        return comments;
    }

    public void setComments(List<PostCommentAPI> comments) {
        this.comments = comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
