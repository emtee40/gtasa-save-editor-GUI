package nl.paulinternet.gtasaveedit.extractor;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import nl.paulinternet.gtasaveedit.view.menu.extractor.ExtractorMenu;
import nl.paulinternet.gtasaveedit.view.window.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;

public class ExtractorServer extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ExtractorServer.class);
    private static final String PROTO_VERSION = "1";
    public static final String FALLBACK_IP = "0.0.0.0";
    private static HttpServer server = null;
    private static final Path tempDir;

    static {
        try {
            tempDir = Files.createTempDirectory("gtasaseExtractor");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(), e.getMessage(),
                    "Unable to create temporary directory!", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Unable to create tempDir!", e);
        }
    }

    private ExtractorMenu menu;
    private JmDNS jmdns;
    private ServiceInfo serviceInfo;

    public ExtractorServer(ExtractorMenu menu) {
        this.menu = menu;
    }

    @Override
    public void run() {
        if (server == null) {
            try {
                startServer();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(MainWindow.getInstance(), e.getMessage(), "Unable to start savegame extractor server!", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            log.error("Server already running! " + server.getAddress().toString());
        }
    }

    private String getPreferredNetworkAddress() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(1); // no need to connect, we only want the interface
            socket.connect(InetAddress.getByName("8.8.8.8"), 666);
            return socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            log.warn("Unable to get preferred network. Using " + FALLBACK_IP + " as server address!", e);
            return FALLBACK_IP;
        }
    }

    private synchronized void startServer() throws IOException {
        String hostAddress = getPreferredNetworkAddress();
        log.info("Starting server on '" + hostAddress + "'");
        server = HttpServer.create(new InetSocketAddress(hostAddress, 0), 0);
        server.createContext("/add", addHandler());
        server.createContext("/upload", uploadHandler());
        server.createContext("/list", listHandler());
        server.createContext("/get", downloadHandler());
        server.createContext("/version", httpExchange -> {
            String response = PROTO_VERSION;
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        });
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();

        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(), e.getMessage(), "Unable to get hostname!", JOptionPane.ERROR_MESSAGE);
            hostName = null;
        }
        if (jmdns == null) {
            jmdns = JmDNS.create(InetAddress.getByName(hostAddress), (hostName != null) ? hostName : "GTA:SA Savegame Editor");
            log.info("Started mDNS as '" + hostName + "' on '" + hostAddress + "'");
        }
        HashMap<String, String> props = new HashMap<>();
        props.put("version", PROTO_VERSION);
        props.put("ip", hostAddress);
        props.put("port", String.valueOf(server.getAddress().getPort()));
        if (hostName != null) {
            props.put("hostname", hostName);
        }
        serviceInfo = ServiceInfo.create("_gtasa-se._tcp.local.", hostAddress.replaceAll("\\.", "-"), server.getAddress().getPort(), 0, 0, props);
        jmdns.registerService(serviceInfo);
    }

    private FormDataHandler addHandler() {
        return new FormDataHandler(d -> {
            Object[] fileData = d.toArray();
            //noinspection ForLoopReplaceableByForEach it's prettier this way
            for (int i = 0; i < fileData.length; i++) {
                FormDataHandler.FileData f = (FormDataHandler.FileData) fileData[i];
                if ("application/octet-stream".equals(f.contentType)) {
                    String fileName = tempDir.toFile().getAbsolutePath() + File.separator + f.fileName;
                    Path filePath = Path.of(fileName);
                    try (OutputStream stream = Files.newOutputStream(filePath)) {
                        log.info("Writing file: '" + filePath + "'");
                        stream.write(f.data);
                        ExtractedSavegameHolder.addSavegame(new ExtractedSavegameFile(filePath.toFile(), f.fileName), menu);
                        JOptionPane.showMessageDialog(MainWindow.getInstance(), "Received file: '" + filePath.toFile().getName() + "' successfully.", "Savegame Received", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(MainWindow.getInstance(), e.getMessage(), "Unable to write temp file!", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    log.info("Unknown part: {name: '" + f.name + "', value: '" + Arrays.toString(f.data) + "'}");
                }
            }
        });
    }

    private HttpHandler uploadHandler() {
        return httpExchange -> {
            String response = "<html><head><title>GTASASE Uploader</title></head><body><form action=\"/add\" " +
                    "method=\"POST\"enctype=\"multipart/form-data\">Select savegame to add:<input type=\"file\" " +
                    "name=\"savegame\" id=\"savegame\"><input type=\"submit\" value=\"Upload\" name=\"submit\"> " +
                    "</form></body></html>";
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        };
    }

    private HttpHandler downloadHandler() {
        return httpExchange -> ExtractedSavegameHolder.getSaveGameFiles().forEach(f -> {
            String[] split = httpExchange.getRequestURI().toString().split("/");
            if (f.fileName.equals(split[split.length - 1])) {
                try (OutputStream os = httpExchange.getResponseBody()) {
                    httpExchange.sendResponseHeaders(200, f.saveGame.length());
                    os.write(Files.readAllBytes(f.saveGame.toPath()));
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(MainWindow.getInstance(), e.getMessage(), "Unable to send file!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private HttpHandler listHandler() {
        return httpExchange -> {
            StringBuilder builder = new StringBuilder("[");
            ExtractedSavegameHolder.getSaveGameFiles().forEach(f ->
                    builder.append("{\"name\": \"").append(f.fileName).append("\"},"));
            builder.append(']');
            String response = builder.toString().replaceAll(",]", "]");
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        };
    }

    public synchronized void stopServer() {
        if (server != null) {
            log.info("Stopping server...");
            server.stop(0);
            if (serviceInfo != null) {
                jmdns.unregisterService(serviceInfo);
                serviceInfo = null;
            }
            jmdns.unregisterAllServices();
            server = null;
            jmdns = null;
        } else {
            log.warn("Server already stopped!");
        }
    }

    static class ExtractedSavegameFile {
        private final File saveGame;
        private final String fileName;

        ExtractedSavegameFile(File saveGame, String fileName) {
            this.saveGame = saveGame;
            this.fileName = fileName;
        }

        File getSaveGame() {
            return saveGame;
        }

        String getFileName() {
            return fileName;
        }
    }

    public static Path getTempDir() {
        return tempDir;
    }
}
