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

    protected String getAuthToken() {
        return authToken;
    }

    protected void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean register(String username, String password, String email) {
        return httpComm.register(username, password, email);
    }

    public boolean login(String username, String password) {
        return httpComm.login(username, password);
    }

    public boolean logout() {
        return httpComm.logout();
    }

    public int createGame(String gameName) {
        return httpComm.createGame(gameName);
    }
}
