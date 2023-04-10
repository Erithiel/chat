
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Chat  {
    public static String readString(String prompt) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            return input;
        }
    }
   
    public static void main(String[] args) {
        Socket sock;
        sock = null;
        boolean server = false;
        while (true) {
            String input = readString(
                    "Enter <port> in order to start the chat server "
                            + "\n or <host>:<port> in order to connect to a running server. "
                            + "\n Enter exit for exiting the chat.\n");
            if (input.equals("exit")) {
                System.out.println("Exiting.");
                return;
            }

            int posColon = input.indexOf(':');
            try {
                if (posColon != -1) {
                    sock = new Socket(
                            InetAddress.getByName(input.substring(0, posColon)),
                            Integer.parseInt(input.substring(posColon + 1)));
                    break;
                } else {
                    int port = Integer.parseInt(input);
                    ServerSocket serverSock = new ServerSocket(port);
                    System.out.println("Server is started, expecting connections");
                    sock = serverSock.accept();
                    serverSock.close();
                    server = true;
                    break;
                }
            } catch (UnknownHostException e) {
                System.out.println("Host unknown, try again!");
            } catch (NumberFormatException e) {
                System.out.println("Port invalid, try again!");
            } catch (ConnectException e) {
                System.out.println("Connection refused, try again!");
            } catch (IOException e) {
                System.out.println("I/O error, try again!");
            }
        }


        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(sock.getInputStream()));
             PrintWriter out = new PrintWriter(sock.getOutputStream(),
                     true)) {
            boolean running = true;
            if (server) {
                System.out.println("Connection established! You may send something.");
                String input = readString("> ");
                if ("exit".equals(input))
                    running = false;
                else
                    out.println(input);
            }
            while (running) {
                String recieved = in.readLine();
                if ("exit".equals(recieved)) {
                    System.out.println("exit received.");
                    break;
                }
                System.out.println(recieved);
                String input = readString("> ");
                if ("exit".equals(input))
                    running = false;
                out.println(input);
            }
            System.out.println("Exiting.");
        } catch (IOException e1) {
            System.out.println("Connection error, exiting.");
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
                System.out.println("Connection refused.");
            }
        }
    }
}
