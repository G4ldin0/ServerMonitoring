package org.cluster;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

public class Service {

    private float CPUPercentage;
    private float memoryPercentage;
    private float responseTime;
    private int activeConnections;


    private float CPUPercentageGain;
    private float memoryPercentageGain;
    private float responseTimeGain;
    private int activeConnectionsGain;

    private final Random numberGenerator;

    public Service()
    {
        numberGenerator = new Random();

        CPUPercentage = numberGenerator.nextFloat();
        memoryPercentage = numberGenerator.nextFloat();
        responseTime = numberGenerator.nextFloat();
        activeConnections = numberGenerator.nextInt(10);

        CPUPercentageGain += (numberGenerator.nextFloat() * 0.1f) - 0.5f;
        memoryPercentageGain += (numberGenerator.nextFloat() * 0.1f) - 0.5f;
        responseTimeGain += (numberGenerator.nextFloat() * 0.1f) - 0.5f;

    }

    public JSONObject getData(){

        // variação de recursos
        CPUPercentageGain += (numberGenerator.nextFloat() * 0.1f) - 0.5f;
        memoryPercentageGain += (numberGenerator.nextFloat() * 0.1f) - 0.5f;
        responseTimeGain += (numberGenerator.nextFloat() * 0.1f) - 0.5f;

        float newConn = numberGenerator.nextFloat() - 0.5f;
        activeConnectionsGain += Math.abs(newConn) > 0.3 ? (int)(Math.abs(newConn)/newConn) : 0;

        // muda valores baseado na variação
        CPUPercentage += Math.max(numberGenerator.nextFloat() * CPUPercentageGain, 0.0f);
        memoryPercentage += Math.max(numberGenerator.nextFloat() * memoryPercentageGain, 0.0f);
        responseTime += Math.max(numberGenerator.nextFloat() * 3.66f * responseTimeGain, 0.0f);
        activeConnections += Math.max(activeConnectionsGain, 0);

        // geração da string JSON para ser enviada
//        HashMap<String, Float> response = new HashMap<>();
        JSONObject response = new JSONObject();
        response.put("cpu_usage", CPUPercentage);
        response.put("memory_usage", memoryPercentage);
        response.put("response_time", responseTime);
        response.put("active_connections", (float) activeConnections);

        // JSON.parse("{cpu: " + CPUPercentage + ", memory: " + memoryPercentage + "}");
        return response;
    }

    public void fix(){
        // conserta erro de uso de recurso anormal
        CPUPercentageGain = 0;
        memoryPercentageGain = 0;
        responseTimeGain = 0;
        activeConnectionsGain = 0;

        CPUPercentage = 0;
        memoryPercentage = 0;
        responseTime = 0;
        activeConnections = 0;
    }


}
