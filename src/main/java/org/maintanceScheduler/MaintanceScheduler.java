package org.maintanceScheduler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.monitoring.Order;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class MaintanceScheduler {
    private static List<Order> orders;
    private final static String FILA = "ordem";

    private static boolean autoAck = true;
    public static void main(String[] args) {
        orders = new ArrayList<>();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(FILA, false,
                    false, false,
                    null);

            DeliverCallback callback = (consumidor, entrega) -> {
                String msg = new String(entrega.getBody(), StandardCharsets.UTF_8);
                orders.add(new Order(msg));
                System.out.println(msg);
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
                    }
            ).start();

            String a;
            Scanner cin = new Scanner(System.in);
            while(true){
                a = cin.next();

                if(a.equalsIgnoreCase("a")){
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }


}
