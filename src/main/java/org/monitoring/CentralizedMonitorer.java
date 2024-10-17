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
import java.util.function.Function;

public class CentralizedMonitorer {
    // var dashboard
    private final List<Order> dashbord;
    private final List<Order>[] infos;
    private final src.RabbitSender rabbitSender;
    public CentralizedMonitorer() throws IOException, TimeoutException {
        dashbord = new ArrayList<>();
        infos = new List[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
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
        String[] FILA = {
                "servidor1/serviço1", "servidor1/serviço2",
                "servidor2/serviço1", "servidor2/serviço2",
                "servidor3/serviço1", "servidor3/serviço2"
        };
        boolean autoAck = true;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        for (String e : FILA)
            channel.queueDeclare(e, false,
                    false, false,
                    null);

        Function<Integer, DeliverCallback> callback = (Integer id) ->
                (consumidor, entrega) -> {
                    String msg = new String(entrega.getBody(), StandardCharsets.UTF_8);
                    System.out.println("Recebi: " + msg);
                    infos[id].add(new Order(msg));
                    this.analyzer(msg, id);
            };

        ;

        new Thread(
                () -> {
                    while (true) {
                        try {
                            for(int i = 0; i < 6; i++)
                                channel.basicConsume(FILA[i], autoAck,
                                        callback.apply(i + 1), consumidor -> {
                                });

//                            channel.basicConsume(FILA, autoAck, callback.apply(2), consumidor -> {});
//                            channel.basicConsume(FILA, autoAck, callback.apply(3), consumidor -> {});

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

    private void analyzer(String msg, int id) {
        Order info = new Order(msg);
        //System.out.println(info.toString());
        if(infos[id].size() >= 2) {
            switch ((String) info.getJson().get("status")) {
                case "azul":
                    break;
                case "amarelo":
                    if(infos[id].get(infos[id].size() - 2).getJson().get("status").equals("azul")){
                        createOrder(info.getJson());
                    }
                    break;
                case "vermelho":
                    if(infos[id].get(infos[id].size() - 2).getJson().get("status").equals("azul") ||
                            infos[id].get(infos[id].size() - 2).getJson().get("status").equals("amarelo") ){
                        createOrder(info.getJson());
                    }
                    break;
            }
        }
    }

    public void createOrder(JSONObject json){
        String issueDescription = "";
        String requiredAction = "";

        JSONObject metric = json.getJSONObject("metrics");

        float cpu = metric.getFloat("cpu_usage");
        float memory = metric.getFloat("memory_usage");
        float response = metric.getFloat("response_time");
        float active = metric.getFloat("active_connections");

        if (cpu > memory && cpu > response && cpu > active) {
            if (cpu > 90) {
                issueDescription = "Alerta crítico: Uso de CPU em " + cpu + "%, o serviço está sobrecarregado!";
                requiredAction = "Ação necessária: Reinicie o serviço e verifique processos intensivos de CPU.";
            } else {
                issueDescription = "Aviso: Uso de CPU em " + cpu + "%, o serviço pode estar lento.";
                requiredAction = "Ação sugerida: Monitore o uso de CPU e otimize processos.";
            }
        } else if (memory > cpu && memory > response && memory > active) {
            if (memory > 90) {
                issueDescription = "Alerta crítico: Uso de memória em " + memory + "%, o serviço está consumindo muita RAM!";
                requiredAction = "Ação necessária: Libere memória e otimize o uso de recursos.";
            } else {
                issueDescription = "Aviso: Uso de memória em " + memory + "%, o serviço pode estar lento.";
                requiredAction = "Ação sugerida: Monitore o uso de memória e otimize processos.";
            }
        } else if (response > cpu && response > memory && response > active) {
            if (response > 1000) {
                issueDescription = "Alerta crítico: Tempo de resposta em " + response + "ms, o serviço está lento!";
                requiredAction = "Ação necessária: Otimize o tempo de resposta e verifique gargalos.";
            } else {
                issueDescription = "Aviso: Tempo de resposta em " + response + "ms, o serviço pode estar lento.";
                requiredAction = "Ação sugerida: Monitore o tempo de resposta e otimize processos.";
            }
        } else if (active > cpu && active > memory && active > response) {
            if (active > 100) {
                issueDescription = "Alerta crítico: Conexões ativas em " + active + ", o serviço está sobrecarregado com conexões!";
                requiredAction = "Ação necessária: Reduza o número de conexões ativas e balanceie a carga.";
            } else {
                issueDescription = "Aviso: Conexões ativas em " + active + ", o serviço pode estar sobrecarregado.";
                requiredAction = "Ação sugerida: Monitore as conexões ativas e balanceie a carga.";
            }
        }

        dashbord.add(new Order(
                LocalDateTime.now().toString(),
                json.getString("server"),
                json.getString("service"),
                json.getString("status"),
                issueDescription,
                requiredAction
        ));

        gerarOrdemServico(dashbord.get(dashbord.size() - 1).toString());
    }
}
