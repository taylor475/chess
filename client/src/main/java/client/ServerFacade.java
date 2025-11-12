package client;

public class ServerFacade {
    HttpCommunicator httpComm;
    String serverDomain;
    String authToken;

    public ServerFacade() throws Exception {
        this("localhost:8080");
    }

    public ServerFacade(String serverDomain) throws Exception {
        this.serverDomain = serverDomain;
        httpComm = new HttpCommunicator(this, serverDomain);
    }
}
