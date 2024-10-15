package org.cluster;

// Classe que representa cluster de serviços, com o agente de monitoramento que
// envia informações constantemente sobre o uso de recurso destes.
public class Cluster {

    // Serviços a ser monitorados
    private Service bancoDeDadosService;
    private Service webService;

    private int clusterId;

    void Cluster(int clusterId) {
        this.clusterId = clusterId;

        // envia dados dos serviços para broker
        // broker[clusterId].sendData(bancoDeDadosService.getData());
        // broker[clusterId].sendData(webService.getData());
    }

}
