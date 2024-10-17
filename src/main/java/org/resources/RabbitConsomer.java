package org.resources;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitConsomer {
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private DeliverCallback callback;
    public RabbitConsomer(){}

    public void connect(String host, String FILA) throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost(host);

        connection = factory.newConnection();
        channel = connection.createChannel();

        this.queueDeclare(FILA);
        this.createCallback();

    }

    private void createCallback() {
        callback = (consumidor, entrega) -> {
            String msg = new String(entrega.getBody(), StandardCharsets.UTF_8);
            System.out.println(msg);
        };
    }

    public DeliverCallback getCallback(){
        return callback;
    }

    public void queueDeclare(String FILA) throws IOException {
        channel.queueDeclare(FILA, false,
                false, false,
                null);
    }

    public Channel getChannel(){
        return channel;
    }
}
