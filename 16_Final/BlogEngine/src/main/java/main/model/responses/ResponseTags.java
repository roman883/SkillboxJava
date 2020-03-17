package main.model.responses;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResponseTags implements ResponseAPI {

    private List<ResponseTagAPI> tags;

    public ResponseTags(Map<String, Double> tagNameAndWeightMap) {
        tags = new LinkedList<>();
        for (String key : tagNameAndWeightMap.keySet()) {
            ResponseTagAPI responseTagAPI = new ResponseTagAPI(key, tagNameAndWeightMap.get(key));
            tags.add(responseTagAPI);
        }
    }

    public List<ResponseTagAPI> getTags() {
        return tags;
    }

    public void setTags(List<ResponseTagAPI> tags) {
        this.tags = tags;
    }

    private static class ResponseTagAPI {

        private String name;
        private double weight;

        private ResponseTagAPI(String name, double weight) {
            this.name = name;
            this.weight = weight;
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
