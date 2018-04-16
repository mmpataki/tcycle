package util.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import util.Logger;

public class HttpServer {

    private String docRoot = ".";
    ServerSocket sock;
    
    public HttpServer(String docRoot) throws IOException {
        this.docRoot = docRoot;
        sock = new ServerSocket(0);
    }

    public String getDocumentRoot() {
        return new File(docRoot).getAbsolutePath();
    }

    public int getRunningPort() {
        return sock.getLocalPort();
    }
    
    /* Runs this http server in the current thread */
    public void run() throws IOException {
        while(true) {
            try (Socket s = sock.accept()) {
                handle(s);
            }
        }
    }

    protected void serve(HttpRequest req) throws IOException {
        sendFile(req.getUrl(), req.getOutputStream());
    }

    public void handle(Socket sock) throws IOException {
        try {
            HttpRequest request = new HttpRequest(sock);
            serve(request);
            sock.close();
        } catch (HttpException ex) {
            Logger.ilog(Logger.LOW, ex.getMessage());
        }
    }

    private void sendFile(String url, OutputStream out) throws IOException {
        FileInputStream fis;
        File file = new File(docRoot + "/" + url);

        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            out.write("HTTP/1.0 404 Not Found\n\n".getBytes());
            return;
        }

        out.write("HTTP/1.0 200 OK\n".getBytes());
        out.write(("Content-Length: " + file.length() + "\n\n").getBytes());
        byte[] buffer = new byte[256];
        int read;
        while ((read = fis.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            if (read < buffer.length) {
                break;
            }
        }
    }
}
