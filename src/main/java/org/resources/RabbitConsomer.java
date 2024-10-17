package org.resources;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import sun.security.smartcardio.SunPCSC;

import java.io.IOException;
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
