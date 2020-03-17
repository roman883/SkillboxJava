package main.model.responses;

import main.model.entities.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ResponsePostsForModeration implements ResponseAPI {

    private int count;
    private List<ResponsePostsApi> posts;

    public ResponsePostsForModeration(int count, ArrayList<Post> postsToShow) {
        this.count = count;
        posts = new ArrayList<>();
        for (Post p : postsToShow) {
            ResponsePostsApi responsePostsApi = new ResponsePostsApi(p);
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

    private static class ResponsePostsApi {

        private int id;
        private String time;
        private PostAuthorUser user;
        private String title;
        private String announce; //TODO Как-то конвертировать в HTML? Не String? какой-то текст анонса - откуда брать?

        public ResponsePostsApi(Post post) {
            this.id = post.getId();
            this.time = getTimeString(post.getTime().toLocalDateTime());
            this.user = new PostAuthorUser(post);
            this.title = post.getTitle();
            this.announce = "Текст анонса поста без HTML-тэгов";
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

        private static class PostAuthorUser {
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

        }
    }
}