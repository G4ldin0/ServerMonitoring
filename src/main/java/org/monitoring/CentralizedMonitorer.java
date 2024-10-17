package org.monitoring;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CentralizedMonitorer {
    // var dashboard
    private List<Order> dashbord;
    public CentralizedMonitorer(){
        dashbord = new ArrayList<>();
    }
    public void monitor() {
        // string json = broker.recieve().parse();

        // se o status for amarelo ou vermelho e mais perigoso que antes:
        // var ordemServico = parser.parse(json);
        // queue.send(ordemServico);

        //dashboard.update(json);

    }

    public void gerarOrdemServico() {
        if(!dashbord.isEmpty()){
            //ObjectMapper
            //try{

            //}
        }
    }

    public void connectLoop(){

    }

    public void addDashbord(Order order){
        dashbord.add(order);
    }

}
