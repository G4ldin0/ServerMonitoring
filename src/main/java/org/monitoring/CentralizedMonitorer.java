package org.monitoring;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONObject;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class CentralizedMonitorer {
    // var dashboard
    private final List<Order> dashbord;
    private final List<Order> infos;
    private final src.RabbitSender rabbitSender;
    public CentralizedMonitorer() throws IOException, TimeoutException {
        dashbord = new ArrayList<>();
        infos = new ArrayList<>();
        this.rabbitSender = new src.RabbitSender();
        try {
            rabbitSender.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }


        //this.debug();
        this.loop();
    }

    public void gerarOrdemServico(String json) {
        try {
            rabbitSender.sendMsg(json);
            System.out.println("Enviei: " + json);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void debug(){
        new Thread( () -> {
            while(true) {
                try {
                    dashbord.add(new Order(
                            "2024-10-04T10:05:00Z",
                            "Servidor 2",
                            "Serviço 2",
                            "Vermelho",
                            "Uso de CPU em 98%, serviço não responde",
                            "Verificar e reiniciar o serviço"
                    ));

                    gerarOrdemServico(dashbord.get(dashbord.size() - 1).toString());

                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public void loop() throws IOException, TimeoutException {
        String FILA = "servidor1/serviço1";
        boolean autoAck = true;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(FILA, false,
                false, false,
                null);


        DeliverCallback callback = (consumidor, entrega) -> {
                String msg = new String(entrega.getBody(), StandardCharsets.UTF_8);
                System.out.println("Recebi: " + msg);
                infos.add(new Order(msg));
                this.analyzer(msg);
        };

        new Thread(
                () -> {
                    while (true) {
                        try {
                            channel.basicConsume(FILA, autoAck,
                                    callback, consumidor -> {
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();

        String a;
        Scanner cin = new Scanner(System.in);
        while(true){
            a = cin.next();

            if(a.equalsIgnoreCase("a")){
                break;
            }
        }
    }

    private void analyzer(String msg) {
        Order info = new Order(msg);
        //System.out.println(info.toString());
        if(infos.size() >= 2) {
            switch ((String) info.getJson().get("status")) {
                case "azul":
                    break;
                case "amarelo":
                    if(infos.get(infos.size() - 2).getJson().get("status").equals("azul")){
                        createOrder(info.getJson());
                    }
                    break;
                case "vermelho":
                    if(infos.get(infos.size() - 2).getJson().get("status").equals("azul") ||
                            infos.get(infos.size() - 2).getJson().get("status").equals("amarelo") ){
                        createOrder(info.getJson());
                    }
                    break;
            }
        }
    }

    public void createOrder(JSONObject json){
        dashbord.add(new Order(
                LocalDateTime.now().toString(),
                json.getString("server"),
                json.getString("service"),
                json.getString("status"),
                "TA FODA MANO!!!!!",
                "DE SEUS PULO!!!!"
        ));

        gerarOrdemServico(dashbord.get(dashbord.size() - 1).toString());
    }
}
