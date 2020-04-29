public class User {

    private int userId;
    private double score;
    private boolean hasShown;


    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isShown() {
        return hasShown;
    }

    public void setHasShown(boolean hasShown) {
        this.hasShown = hasShown;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User(double score, int userId) {
        this.userId = userId;
        this.score = score;
    }
}

