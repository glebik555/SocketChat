import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Server {

    public static final int PORT = 8080;
    public static Map<ServerSomthing, String> serverMap = new HashMap<>(); // список всех нитей


    public static Connection connectToPSQL() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";

        Properties props = new Properties();
        props.setProperty("user","glebasta");
        props.setProperty("password","123321");
        props.setProperty("ssl","disable");
        Connection conn = DriverManager.getConnection(url,props);
        return conn;
    }
    public static void main(String[] args) throws IOException, SQLException {


        Connection conn = connectToPSQL();
        if (conn != null){
            System.out.println("You successfully connected to database now");
        }else{
            System.out.println("Failed to make connection to database");
        }
        String SQL_SELECT = "Select * from credentials";
        PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT);
            ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("login"));
            System.out.println(resultSet.getString("password"));
        }

        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Start server!");
        try {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                try {
                    serverMap.put(new ServerSomthing(socket), "Anonymous"); // добавить новое соединенние в список
                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет,
                    // в противном случае, нить закроет его при завершении работы:
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}