package main.api.response;

import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.TagToPost;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class ResponsePost implements ResponseApi {

    private int id;
    private String time;
    private PostAuthorApi user;
    private String title;
    private String announce; //TODO Как-то конвертировать в HTML? Не String?
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private List<PostCommentApi> comments;
    private List<String> tags;

    public ResponsePost(Post post) {
        id = post.getId();
        time = getTimeString(post.getTime().toLocalDateTime());
        user = new PostAuthorApi(post.getUser().getId(), post.getUser().getName());
        title = post.getTitle();
        announce = post.getText().length() < 200 ? post.getText(): post.getText().substring(0, 200) + "..."; //TODO аннонс - 200 символов из текста поста
        likeCount = (int) post.getPostVotes().stream().filter(l -> l.getValue() == 1).count();
        dislikeCount = (int) post.getPostVotes().stream().filter(l -> l.getValue() == -1).count();
        commentCount = post.getPostComments().size();
        viewCount = post.getViewCount();
        comments = new LinkedList<>();
        tags = new LinkedList<>();
        for (PostComment comment : post.getPostComments()) {
            int commentAuthorId = comment.getUser().getId();
            String commentAuthorName = comment.getUser().getName();
            String commentAuthorPhoto = comment.getUser().getPhoto();
            PostCommentApi.PostCommentAuthorApi commentAuthor =
                    new PostCommentApi.PostCommentAuthorApi(commentAuthorId, commentAuthorName, commentAuthorPhoto);
            int commentId = comment.getId();
            String commentTime = getTimeString(comment.getTime().toLocalDateTime());
            String commentText = comment.getText();
            PostCommentApi postCommentAPI = new PostCommentApi(commentId, commentTime, commentAuthor, commentText);
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
        if (objectCreatedTime.isAfter(LocalDate.now().atStartOfDay())
                && objectCreatedTime.isBefore(LocalDateTime.now())) {
            timeString.append("Сегодня, ").append(objectCreatedTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else if (objectCreatedTime.isAfter(LocalDate.now().atStartOfDay().minusDays(1))) {
            timeString.append("Вчера, ").append(objectCreatedTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            timeString.append(objectCreatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm")));
        }
        return timeString.toString();
    }

    // Конструктор, геттеры и сеттеры. Не используются (пока)


    public ResponsePost(int id, String time, PostAuthorApi user, String title, String announce, int likeCount,
                        int dislikeCount, int commentCount, int viewCount, List<PostCommentApi> comments, List<String> tags) {
        this.id = id;
        this.time = time;
        this.user = user;
        this.title = title;
        this.announce = announce;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.commentCount = commentCount;
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

    public PostAuthorApi getUser() {
        return user;
    }

    public void setUser(PostAuthorApi user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnounce() {
        return announce;
    }

    public void setAnnounce(String announce) {
        this.announce = announce;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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

    public List<PostCommentApi> getComments() {
        return comments;
    }

    public void setComments(List<PostCommentApi> comments) {
        this.comments = comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    static class PostAuthorApi {

        private int id;
        private String name;

        public PostAuthorApi(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class PostCommentApi {

        private int id;
        private String time;
        private PostCommentAuthorApi user;
        private String text;

        public PostCommentApi(int id, String time, PostCommentAuthorApi user, String text) {
            this.id = id;
            this.time = time;
            this.user = user;
            this.text = text;
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

        public PostCommentAuthorApi getUser() {
            return user;
        }

        public void setUser(PostCommentAuthorApi user) {
            this.user = user;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        static class PostCommentAuthorApi {

            private int id;
            private String name;
            private String photo;

            public PostCommentAuthorApi(int id, String name, String photo) {
                this.id = id;
                this.name = name;
                this.photo = photo;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPhoto() {
                return photo;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }
        }
    }
}