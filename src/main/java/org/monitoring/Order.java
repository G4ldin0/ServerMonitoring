package org.monitoring;

import org.json.JSONObject;

public class Order {
    private JSONObject json;
    public Order(){}

    public Order(String json){
        this.json = new JSONObject(json);
    }
    public Order(String timestamp, String server, String service, String status, String problem, String action_required){
        json = new JSONObject();
        json.put("Timestamp", timestamp);
        json.put("Server", server);
        json.put("Service", service);
        json.put("Status", status);
        json.put("Problem", problem);
        json.put("Action_Required",action_required);
    }

    public void setJson(JSONObject json){
        this.json = json;
    }

    public JSONObject getJson() {
        return json;
    }

    public String toString(){
        return json.toString();
    }

}
