package org.monitoring;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class MonitorerRun {
    public static void main(String[] args) {
        try {
            new CentralizedMonitorer();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}


