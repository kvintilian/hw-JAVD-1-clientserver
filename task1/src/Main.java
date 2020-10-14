import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws InterruptedException {


//        Thread server = new Thread(null, Main::startServer, "server");
//        server.start();
//        Thread client = new Thread(null, Main::startClient, "client");
//        client.start();
//        server.join();

       
    }


    public static void startServer() {
        // Занимаем порт, определяя серверный сокет
        try {
            ServerSocket servSocket = new ServerSocket(23444);
            while (true) {
                // Ждем подключения клиента и получаем потоки для дальнейшей работы
                try (Socket socket = servSocket.accept();
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        // Пишем ответ
                        out.println("Echo: " + line);
                        // Выход если от клиента получили end
                        if (line.equals("end")) {
                            break;
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(System.out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startClient() {
        // Определяем сокет сервера
        try {
            Socket socket = new Socket("127.0.0.1", 23444);
            // Получаем входящий и исходящий потоки информации
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()), true); Scanner scanner = new Scanner(System.in)) {
                String msg;
                while (true) {
                    System.out.println("Enter message for server...");
                    msg = scanner.nextLine();
                    out.println(msg);
                    if ("end".equals(msg)) break;
                    System.out.println("SERVER: " + in.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
