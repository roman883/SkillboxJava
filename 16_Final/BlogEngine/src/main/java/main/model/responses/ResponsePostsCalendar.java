package main.model.responses;

import java.sql.Date;
import java.util.*;

public class ResponsePostsCalendar implements ResponseAPI {

    private List<Integer> years;
    private HashMap<String, Integer> posts;

    public ResponsePostsCalendar(HashMap<Date, Integer> postsCountByDate, TreeSet<Integer> allYears) {
        years = new LinkedList<>();
        years.addAll(allYears);
        years.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        posts = new HashMap<>();
        for (Date d : postsCountByDate.keySet()) {
            posts.put(String.valueOf(d), postsCountByDate.get(d));
        }
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public HashMap<String, Integer> getPosts() {
        return posts;
    }

    public void setPosts(HashMap<String, Integer> posts) {
        this.posts = posts;
    }
}
