package main.api.response;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResponseTags implements ResponseApi {

    private List<ResponseTagApi> tags;

    public ResponseTags(Map<String, Double> tagNameAndWeightMap) {
        tags = new LinkedList<>();
        for (String key : tagNameAndWeightMap.keySet()) {
            ResponseTagApi responseTagAPI = new ResponseTagApi(key, tagNameAndWeightMap.get(key));
            tags.add(responseTagAPI);
        }
    }

    @Override
    public String toString() {
        return "ResponseTags{" +
                "tags=" + tags +
                '}';
    }

    public List<ResponseTagApi> getTags() {
        return tags;
    }

    public void setTags(List<ResponseTagApi> tags) {
        this.tags = tags;
    }

    static class ResponseTagApi {

        private String name;
        private double weight;

        private ResponseTagApi(String name, double weight) {
            this.name = name;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "ResponseTagApi{" +
                    "name='" + name + '\'' +
                    ", weight=" + weight +
                    '}';
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }
    }
}
