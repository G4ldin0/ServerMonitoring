package org.cluster;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Classe que representa cluster de serviços, com o agente de monitoramento que
// envia informações constantemente sobre o uso de recurso destes.
public class Cluster {

    // Serviços a ser monitorados
    private Service bancoDeDadosService;
    private Service webService;

    private int clusterId;

    private ScheduledExecutorService executor;

    ConnectionFactory factory = new ConnectionFactory();
    Connection connection;

    Channel channel;

    private final String HOST = "localhost";

    public Cluster(int clusterId) {
        this.clusterId = clusterId;

        bancoDeDadosService = new Service();
        webService = new Service();

        try {
            factory.setHost(HOST);

            connection = factory.newConnection();
            channel = connection.createChannel();

            // está errado
            channel.queueDeclare("servidor" + clusterId + "/serviço1", false, false, false, null);
            channel.queueDeclare("servidor" + clusterId + "/serviço2", false, false, false, null);


        } catch (Exception e) {
            e.printStackTrace();
        }

        executor = Executors.newScheduledThreadPool(3);
        executor.scheduleAtFixedRate(this::getData, 0, 10, TimeUnit.SECONDS);

        fixServer();

    }

    void getData() {

        try {

            int i = 1;

            JSONObject serviceData = bancoDeDadosService.getData();

            float cpu = serviceData.getFloat("cpu_usage");
            float memory = serviceData.getFloat("memory_usage");
            float response = serviceData.getFloat("response_time");
            float active = serviceData.getFloat("active_connections");

            float status = cpu * 1.4f + memory * 1.2f + (response / 3.66f) * 1.3f + active * 1.1f;
            status = status / 4;

            String statusStr = "azul";
            if (status > 10.0f) {
                statusStr = "vermelho";
            } else if (status > 3.0f) {
                statusStr = "amarelo";
            }

            JSONObject data = new JSONObject();
            data.put("timestamp", LocalDateTime.now());
            data.put("service", "Serviço " + i++);
            data.put("status", statusStr);
            data.put("server", "Servidor " + clusterId);
            data.put("metrics", serviceData);


            channel.basicPublish("", "servidor" + clusterId + "/serviço1", null, data.toString().getBytes(StandardCharsets.UTF_8));


            serviceData = webService.getData();

            cpu = serviceData.getFloat("cpu_usage");
            memory = serviceData.getFloat("memory_usage");
            response = serviceData.getFloat("response_time");
            active = serviceData.getFloat("active_connections");

            status = cpu * 1.4f + memory * 1.2f + (response / 3.66f) * 1.3f + active * 1.1f;
            status = status / 4;

            statusStr = "azul";
            if (status > 10.0f) {
                statusStr = "vermelho";
            } else if (status > 3.0f) {
                statusStr = "amarelo";
            }

            data = new JSONObject();
            data.put("timestamp", LocalDateTime.now());
            data.put("service", "Serviço " + i++);
            data.put("status", statusStr);
            data.put("server", "Servidor " + clusterId);
            data.put("metrics", serviceData);

            channel.basicPublish("", "servidor" + clusterId + "/serviço2", null, data.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    void fixServer(){
        // conserta erro de uso de recurso anormal
        Scanner in = new Scanner(System.in);
        System.out.println("Press f to fix");
        while (true) {
            String next = in.next();
            if (next.equals("f")) {
                bancoDeDadosService.fix();
                webService.fix();
                System.out.println("Fixed");
            }
        }
    }

    public static void main(String[] args) {
        Cluster cluster = new Cluster(2);
        cluster.getData();
    }

    public static class TesteReceberDados{
        public static void main(String[] args) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel()) {
//                channel.queueDeclare("servidor1/serviço1", false, false, false, null);

                channel.basicConsume("servidor1/serviço1", true, (consumerTag, message) -> {
                    String m = new String(message.getBody(), StandardCharsets.UTF_8);
                    JSONObject teste = new JSONObject(m);
                    System.out.println("Recebido: " + teste);
                }, consumerTag -> { });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
