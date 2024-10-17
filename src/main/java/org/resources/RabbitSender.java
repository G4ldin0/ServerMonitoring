package src;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitSender {

    private static ServerSocket s;
    private final static String FILA = "ordem";
    private static ConnectionFactory connectionFactory;

    private static Connection connection;

    private static Channel channel;

    public RabbitSender(){

    }

    public  void connect() throws IOException, TimeoutException {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(FILA, false,false,false, null);
    }

    public  void sendMsg(String msg) throws IOException, InterruptedException {
        channel.basicPublish("", FILA, null, msg.getBytes(StandardCharsets.UTF_8));
    }
}