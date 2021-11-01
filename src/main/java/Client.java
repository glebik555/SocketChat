import java.io.*;
import java.net.Socket;

public class Client extends Thread {

    private static Socket clientSocket; //сокет для общения
    private static String name;
    private static BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
    // мы узнаем что хочет сказать клиент?
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

    public Client(Socket socket) throws IOException {
        // если потоку ввода/вывода приведут к генерированию исключения, оно проброситься дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start(); // вызываем run()
    }

    public static void main(String[] args) {
        try {
                // адрес - локальный хост, порт - 8080, такой же как у сервера
                clientSocket = new Socket("localhost", 8080); // этой строкой мы запрашиваем
                //  у сервера доступ на соединение
                reader = new BufferedReader(new InputStreamReader(System.in));
                // читать соообщения с сервера
                new Client(clientSocket);
                name = "";
                while (true) {
                    Thread.sleep(1000);
                    if (name.equals("")) {
                        System.out.println("Вы что-то хотели сказать? Введите это здесь:");
                    }else{
                        System.out.println(name + ", вы что-то хотели сказать? Введите это здесь:");
                    }
                    // если соединение произошло и потоки успешно созданы - мы можем
                    //  работать дальше и предложить клиенту что то ввести
                    // если нет - вылетит исключение
                    String word = reader.readLine(); // ждём пока клиент что-нибудь
                    if (word.contains("@Name")){
                        name = word.split(" ")[1];
                    }
                    // не напишет в консоль
                    out.write(word + "\n"); // отправляем сообщение на сервер
                    out.flush();
                }
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
        }
    }

    @Override
    public void run(){

        try {
            while (true) {
                if (in.ready()) {
                    String serverWord = in.readLine();
                    if (!serverWord.equals("You left chat!")) {
                        System.out.println(serverWord + "@");
                    } else {
                        System.out.println("You left chat!");
                        clientSocket.close();
                        in.close();
                        out.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}