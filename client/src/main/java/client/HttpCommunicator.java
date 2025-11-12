package client;

public class HttpCommunicator {
    String baseUrl;
    ServerFacade facade;

    public HttpCommunicator(ServerFacade facade, String serverDomain) {
        baseUrl = "http://" + serverDomain;
        this.facade = facade;
    }
}
