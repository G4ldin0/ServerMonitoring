package org.monitoring;

public class Order {
    private String timeStamp;
    private String server;
    private String status;
    private String problem;
    private String actionRequired;
    public Order(){}

    public Order(String time, String ser, String stat, String probl, String act){
        this.timeStamp = time;
        this.server = ser;
        this.status = stat;
        this.problem = probl;
        this.actionRequired = act;
    }


    public String getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(String actionRequired) {
        this.actionRequired = actionRequired;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
