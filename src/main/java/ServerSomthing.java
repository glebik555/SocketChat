import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

class ServerSomthing extends Thread {

    private Socket socket; // сокет, через который сервер общается с клиентом,
    // кроме него - клиент и сервер никак не связаны
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    private Connection conn;

    public static Connection connectToPSQL() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";

        Properties props = new Properties();
        props.setProperty("user","glebasta");
        props.setProperty("password","123321");
        props.setProperty("ssl","disable");
        Connection conn = DriverManager.getConnection(url,props);
        return conn;
    }

    public ServerSomthing(Socket socket) throws IOException, SQLException {
        this.socket = socket;
        // если потоку ввода/вывода приведут к генерированию исключения, оно проброситься дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        conn = connectToPSQL();
        if (conn != null){
            System.out.println("You successfully connected to database now");
        }else{
            System.out.println("Failed to make connection to database");
        }
//        String SQL_SELECT = "Select * from credentials";
//        PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT);
//        ResultSet resultSet = preparedStatement.executeQuery();
//        while (resultSet.next()) {
//            System.out.println(resultSet.getString("login"));
//            System.out.println(resultSet.getString("password"));
//        }
        start(); // вызываем run()
    }
    @Override
    public void run() {
        String word;
        try {
            while (true) {
                word = in.readLine();
                for (Map.Entry<ServerSomthing,String> pair : Server.serverMap.entrySet()) {
                    if(!word.equals("!stop")) {
                        if(word.contains("@Name")){
                            pair.setValue(word.split(" ")[1]);
                            pair.getKey().send("You change name to " + pair.getValue());
                            continue;
                        }
                        if(word.contains("@login")){
                            System.out.println("Tyt");
                            String SQL_SELECT = "SELECT * FROM credential";  // Не работает!
                            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT);
                            ResultSet resultSet = preparedStatement.executeQuery();
                            while(resultSet.next()){
                                System.out.println(resultSet.getString("login"));
                            }
                        }

                        System.out.println(pair.getValue() + " said: " + word);
                        pair.getKey().send(pair.getValue() + " said: " + word); // отослать принятое сообщение с
                        // привязанного клиента всем остальным включая его

                    }else{
                        send("You left chat!");
                        System.out.println("Client left!");
                        pair.getKey().socket.close();
                        break;
                    }
                }
            }

        } catch (IOException | SQLException e) {
        }
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }
}