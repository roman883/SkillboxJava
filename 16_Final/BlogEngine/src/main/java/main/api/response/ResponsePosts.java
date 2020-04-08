package main.api.response;

import main.model.entities.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ResponsePosts implements ResponseApi {

    private int count;
    private List<ResponsePostsApi> posts;

    public ResponsePosts(int count, ArrayList<Post> postsToShow, int announceLength) {
        this.count = count;
        posts = new ArrayList<>();
        for (Post p : postsToShow) {
            ResponsePostsApi responsePostsApi = new ResponsePostsApi(p, announceLength);
            posts.add(responsePostsApi);
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ResponsePostsApi> getPosts() {
        return posts;
    }

    public void setPosts(List<ResponsePostsApi> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "count=" + count +
                ", posts=" + posts +
                '}';
    }

    static class ResponsePostsApi {

        private int id;
        private String time;
        private PostAuthorUser user;
        private String title;
        private String announce;
        private int likeCount;
        private int dislikeCount;
        private int commentCount;
        private int viewCount;

        public ResponsePostsApi(Post post, int announceLength) {
            this.id = post.getId();
            this.time = getTimeString(post.getTime());
            this.user = new PostAuthorUser(post);
            this.title = post.getTitle();
            String temp = post.getText().replaceAll("<.+?>", "");
            announce = temp.length() < announceLength ? temp
                    : temp.substring(0, announceLength) + "...";
            this.likeCount = (int) post.getPostVotes().stream().filter(l -> l.getValue() == 1).count();
            this.dislikeCount = (int) post.getPostVotes().stream().filter(l -> l.getValue() == -1).count();
            this.commentCount = post.getPostComments().size();
            this.viewCount = post.getViewCount();
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

        public PostAuthorUser getUser() {
            return user;
        }

        public void setUser(PostAuthorUser user) {
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

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public int getViewCount() {
            return viewCount;
        }

        public void setViewCount(int viewCount) {
            this.viewCount = viewCount;
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

        @Override
        public String toString() {
            return "ResponsePostsApi{" +
                    "id=" + id +
                    ", time='" + time + '\'' +
                    ", user=" + user +
                    ", title='" + title + '\'' +
                    ", announce='" + announce + '\'' +
                    ", likeCount=" + likeCount +
                    ", dislikeCount=" + dislikeCount +
                    ", commentCount=" + commentCount +
                    ", viewCount=" + viewCount +
                    '}';
        }

        static class PostAuthorUser {
            private int id;
            private String name;

            public PostAuthorUser(Post post) {
                this.id = post.getUser().getId();
                this.name = post.getUser().getName();
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

            @Override
            public String toString() {
                return "PostAuthorUser{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }
}