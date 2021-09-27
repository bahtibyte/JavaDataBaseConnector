package oop;

import java.util.ArrayList;

public class Login {

    public static final Login EMPTY = new Login("","","","","", new ArrayList<String>());

    private String nickname;
    private String address;
    private String port;
    private String username;
    private String password;

    private ArrayList<String> databases;

    public Login(String nickname, String address, String port, String username, String password, ArrayList<String> databases) {
        this.nickname = nickname;
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.databases = databases;
    }

    public void setLogin(Login login){
        this.nickname = login.nickname;
        this.address = login.address;
        this.port = login.port;
        this.username = login.username;
        this.password = login.password;
        this.databases = login.databases;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getDatabases() {
        return databases;
    }

    public void setDatabases(ArrayList<String> databases) {
        this.databases = databases;
    }

    @Override
    public String toString() {
        return "Login{" +
                "nickname='" + nickname + '\'' +
                ", address='" + address + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", databases=" + databases +
                '}';
    }
}
