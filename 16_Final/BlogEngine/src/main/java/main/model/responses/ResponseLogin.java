package main.model.responses;

import main.model.entities.User;

public class ResponseLogin implements ResponseAPI {

    private boolean result;
    private ResponseLoginUserApi user;

    public ResponseLogin(User user) {
        result = true;
        this.user = new ResponseLoginUserApi(user);
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ResponseLoginUserApi getUser() {
        return user;
    }

    public void setUser(ResponseLoginUserApi user) {
        this.user = user;
    }

    private static class ResponseLoginUserApi {

        private int id;
        private String name;
        private String photo;
        private String email;
        private boolean moderation;
        private int moderationCount;
        private boolean settings;

        private ResponseLoginUserApi(User user) {
            id = user.getId();
            name = user.getName();
            photo = user.getPhoto();
            email = user.getEmail();
            moderation = user.isModerator();
            moderationCount = user.getPostsModerated().size();
            settings = user.isModerator();
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isModeration() {
            return moderation;
        }

        public void setModeration(boolean moderation) {
            this.moderation = moderation;
        }

        public int getModerationCount() {
            return moderationCount;
        }

        public void setModerationCount(int moderationCount) {
            this.moderationCount = moderationCount;
        }

        public boolean isSettings() {
            return settings;
        }

        public void setSettings(boolean settings) {
            this.settings = settings;
        }
    }
}
