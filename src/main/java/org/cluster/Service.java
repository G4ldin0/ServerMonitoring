package org.cluster;

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

    }

    public String getData(){

        // variação de recursos
        CPUPercentageGain += (numberGenerator.nextFloat() * 0.1f) - 0.5f;
        memoryPercentageGain += (numberGenerator.nextFloat() * 0.1f) - 0.5f;
        responseTimeGain += (numberGenerator.nextFloat() * 0.1f) - 0.5f;

        float newConn = numberGenerator.nextFloat() - 0.5f;
        activeConnectionsGain += Math.abs(newConn) > 0.3 ? (int)(Math.abs(newConn)/newConn) : 0;

        // muda valores baseado na variação
        CPUPercentage += numberGenerator.nextFloat() * CPUPercentageGain;
        memoryPercentage += numberGenerator.nextFloat() * memoryPercentageGain;
        responseTime += numberGenerator.nextFloat() * responseTimeGain;
        activeConnections += activeConnectionsGain;

        // geração da string JSON para ser enviada
        HashMap<String, Float> response = new HashMap<>();
        response.put("cpu_usage", CPUPercentage);
        response.put("memory_usage", memoryPercentage);
        response.put("response_time", responseTime);
        response.put("active_connections", (float) activeConnections);

        // JSON.parse("{cpu: " + CPUPercentage + ", memory: " + memoryPercentage + "}");
        return "";
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
