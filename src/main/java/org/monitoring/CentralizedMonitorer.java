package org.monitoring;

import org.json.JSONObject;
import org.resources.RabbitConsomer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class CentralizedMonitorer {
    // var dashboard
    private final List<Order> dashbord;
    private final src.RabbitSender rabbitSender;
    public CentralizedMonitorer() throws IOException, TimeoutException {
        dashbord = new ArrayList<>();
        this.rabbitSender = new src.RabbitSender();
        try {
            rabbitSender.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }


        this.debug();
        this.loop();
    }
    public void monitor() {
        // string json = broker.recieve().parse();

        // se o status for amarelo ou vermelho e mais perigoso que antes:
        // var ordemServico = parser.parse(json);
        // queue.send(ordemServico);

        //dashboard.update(json);

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
        RabbitConsomer consomer = new RabbitConsomer();
        consomer.connect("localhost", "servidor1/serviço1");
        while (true){

        }
    }
}
