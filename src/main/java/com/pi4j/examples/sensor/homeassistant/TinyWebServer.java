package com.pi4j.examples.sensor.homeassistant;

import javafx.util.Pair;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** A tiny embedded http 1.0 server without any capabilities except serving a bunch of files with generated content. */
public class TinyWebServer {
    private final int port;
    private final ServerSocket serverSocket;
    private final Map<String, Pair<String, Consumer<PrintWriter>>> handlers = new HashMap<>();

    /** Creates a new web server on the given port. Note that run() needs to still be called to handle requests */
    public TinyWebServer(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    /** Returns the URL of this server as a String */
    public String getUrl() {
        try (Socket socket = new Socket()) {
            // Get our own address, see https://stackoverflow.com/a/41822127/1401879
            socket.connect(new InetSocketAddress("google.com", 80));
            return "http:/" + socket.getLocalAddress() + ":" + port;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Add a handler for a given path producing the given content type, writing to the given print writer. */
    public void addRequestHandler(String path, String contentType, Consumer<PrintWriter> handler) {
        handlers.put(path, new Pair(contentType, handler));
    }

    private void handleRequest(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        String request = in.readLine();
        System.out.println(new Date() + ": Request from " + socket.getRemoteSocketAddress() + ": " + request);
        while (true) {
            String line = in.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
        }
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            @Override
            public void println() {
                write("\r\n");
            }
        };
        if (request == null || !request.startsWith("GET /")) {
            out.println("HTTP/1.0 501 Error");
        } else {
            int end = request.lastIndexOf(' ');
            String path = request.substring(5, end);

            Pair<String, Consumer<PrintWriter>> handler = handlers.get(path);
            if (handler == null) {
                out.println("HTTP/1.0 404 Not Found");
            } else {
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: " + handler.getKey() + "; charset=utf-8");
                out.println();
                handler.getValue().accept(out);
            }
        }
        out.println();
        out.close();
    }

    /** Handles requests in the current thread. */
    public void run() {
        while(true) {
            try(Socket socket = serverSocket.accept()) {
                handleRequest(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
