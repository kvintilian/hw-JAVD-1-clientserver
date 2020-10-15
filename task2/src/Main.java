import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 40004;

    public static void main(String[] args) throws InterruptedException {
        new Thread(null, Main::startServer, "server").start();
        new Thread(null, Main::startClient, "client").start();
    }

    public static void startServer() {
        try {
            final ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(HOST, PORT));
            while (!Thread.currentThread().isInterrupted()) {
                try (SocketChannel socketChannel = serverChannel.accept()) {
                    final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                    while (socketChannel.isConnected()) {
                        int bytesCount = socketChannel.read(inputBuffer);
                        if (bytesCount == -1) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                        final String msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                        inputBuffer.clear();
                        socketChannel.write(ByteBuffer.wrap((msg.replaceAll("\\s+", "")).getBytes(StandardCharsets.UTF_8)));
                    }
                } catch (IOException err) {
                    System.out.println(err.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public static void startClient() {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(HOST, PORT);
            final SocketChannel socketChannel = SocketChannel.open();
            try (socketChannel; Scanner scanner = new Scanner(System.in)) {
                socketChannel.connect(socketAddress);
                final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                String msg;
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("Введите строку для уничтожения пробелов или end для выхода:");
                    msg = scanner.nextLine();
                    if ("end".equals(msg)) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                    int bytesCount = socketChannel.read(inputBuffer);
                    System.out.println(new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8).trim());
                    inputBuffer.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
