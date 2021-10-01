package jdbc.oop;

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

    public String getAddress() {
        return address;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getDatabases() {
        return databases;
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

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Login)){
            return false;
        }
        Login l = (Login) o;
        return l.address.equals(address) && l.nickname.equals(nickname) && l.port.equals(port) && l.username.equals(username)
                && l.password.equals(password);
    }
}
