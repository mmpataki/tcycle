package tcycle;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import jsonparser.DictObject;
import jsonparser.Json;
import jsonparser.JsonArray;
import jsonparser.JsonException;
import jsonparser.JsonObject;
import util.Logger;
import util.http.HttpRequest;
import util.http.HttpServer;

public class TCycle extends HttpServer {

    int cur_id = 0;
    JsonArray data;

    @Override
    public void serve(HttpRequest req) {
        try {
            String[] chunks = req.getUrl().split("/");
            switch (chunks[0]) {
                case "delete":
                    do_delete(req);
                    break;
                case "get":
                    do_get(req);
                    break;
                case "put":
                    do_put(req);
                    break;
                case "resched":
                    do_resched(req);
                    break;
                default:
                    super.serve(req);
            }
        } catch (Exception ex) {
            Logger.elog(Logger.HIGH, req.getUrl() + " " + ex.getMessage());
        }
    }

    public TCycle(String docRoot) throws IOException {
        super(docRoot);
        data = new JsonArray();
    }

    private void do_delete(HttpRequest req) throws IOException {
        String status = "";
        OutputStream out = req.getOutputStream();
        out.write("HTTP/1.0 200 OK\n".getBytes());
        try {
            DictObject obj = (DictObject) Json.parse(req.getData());
            int id = (int) obj.get("id").getValue();
            for (int i = 0; i < data.size(); i++) {
                if ((int) ((DictObject) data.get(i)).get("id").getValue() == id) {
                    data.remove(i);
                    break;
                }
            }
            status = "{\"status\": \"success\"}";
        } catch (Exception ex) {
            Logger.elog(Logger.DEBUG, ex.getMessage());
            status = "{\"status\": \"failure\"}";
        }
        out.write(("Content-Length: " + status.length() + "\n\n").getBytes());
        out.write(status.getBytes());
    }

    private void do_get(HttpRequest req) throws IOException {
        OutputStream out = req.getOutputStream();
        String buf = data.toString();
        out.write("HTTP/1.0 200 OK\n".getBytes());
        out.write(("Content-Length: " + buf.length() + "\n\n").getBytes());
        out.write(buf.getBytes());
    }

    private void do_put(HttpRequest req) throws IOException {
        try {
            JsonObject jobj = Json.parse(req.getData());
            ((DictObject) jobj).set("id", cur_id++);
            data.add(jobj);

            OutputStream out = req.getOutputStream();
            String str = jobj.toString();
            out.write("HTTP/1.0 200 OK\n".getBytes());
            out.write(("Content-Length: " + str.length() + "\n\n").getBytes());
            req.getOutputStream().write(str.getBytes());
        } catch (JsonException ex) {
            Logger.elog(Logger.DEBUG, ex.getMessage());
        }
    }

    private void do_resched(HttpRequest req) throws IOException {
        OutputStream out = req.getOutputStream();
        String str = "{\"status\":\"failure\"}";
        try {
            DictObject dobj = (DictObject) Json.parse(req.getData());
            int i, id = (int) dobj.get("id").getValue();
            for (i = 0; i < data.size(); i++) {
                if ((int) ((DictObject) data.get(i)).get("id").getValue() == id) {
                    break;
                }
            }
            JsonObject jobj = data.get(i);
            data.remove(i);
            data.add(jobj);
            str = "{\"status\":\"success\"}";
        } catch (JsonException ex) {
            Logger.elog(Logger.HIGH, "Error while putting while. " + ex.getMessage());
        }
        out.write("HTTP/1.0 200 OK\n".getBytes());
        out.write(("Content-Length: " + str.length() + "\n\n").getBytes());
        out.write(str.getBytes());
    }

}
