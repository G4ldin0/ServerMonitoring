package org.monitoring;

import org.json.JSONObject;

public class MonitorerRun {
    public static void main(String[] args) {
        System.out.println("oh lapada seca!!!!!!!!!!!!!");
        JSONObject obj = new JSONObject();
        obj.put("SITUAÇÃO", "JSON X XML");
        obj.put("Ano", 2002);
        obj.put("genero", "Ação");

        String json = obj.toString();
        System.out.println(json);

        System.out.println(obj.get("Ano"));
    }
}
