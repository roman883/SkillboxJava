package main.api.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseStatistics implements ResponseApi {

    private Map<String, Object> map;

    public ResponseStatistics(int postsCount, int allLikesCount, int allDislikeCount, int viewsCount,
                              String firstPublicationDate) {
        this.map = new HashMap<>();
        map.put("Постов", postsCount);
        map.put("Лайков", allLikesCount);
        map.put("Дизлайков", allDislikeCount);
        map.put("Просмотров", viewsCount);
        map.put("Первая публикация", firstPublicationDate);
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
