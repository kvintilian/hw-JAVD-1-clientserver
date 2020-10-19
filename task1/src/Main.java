import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 40004;

    public static void main(String[] args) {
        /* Используем Blocking способ взаимодейтсвия, так как по задаче нет необходимости в асинхронном вводе-выводе  */
        new Thread(null, Main::startServer, "server").start();
        new Thread(null, Main::startClient, "client").start();
    }

    public static String fibonacci(int number) {
        BigInteger n0 = new BigInteger("1");
        BigInteger n1 = new BigInteger("1");
        BigInteger n2 = new BigInteger("0");
        for (int i = 3; i <= number; i++) {
            n2 = n0.add(n1);
            n0 = n1;
            n1 = n2;
        }
        return n2.toString();
    }

    public static void startServer() {
        try {
            ServerSocket servSocket = new ServerSocket(PORT);
            while (!Thread.currentThread().isInterrupted()) {
                try (Socket socket = servSocket.accept();
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.equals("end")) {
                            Thread.currentThread().interrupt();
                            out.println("Сервер остановлен");
                        } else {
                            try {
                                int number = Integer.parseInt(line.trim());
                                out.println("Это число: " + fibonacci(number));
                            } catch (NumberFormatException ignored) {
                                out.println("Повторите ввод!");
                            }
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(System.out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public static void startClient() {
        try {
            Socket socket = new Socket(HOST, PORT);
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()), true); Scanner scanner = new Scanner(System.in)) {
                String msg;
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("Введите номер числа Фибоначчи или end для выхода:");
                    msg = scanner.nextLine();
                    out.println(msg);
                    if ("end".equals(msg)) Thread.currentThread().interrupt();
                    System.out.println(in.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
