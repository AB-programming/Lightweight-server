package com.abstyle.Server;

import com.abstyle.Model.Request;
import com.abstyle.Model.Response;
import com.abstyle.TomcatApplication;
import com.abstyle.Util.AnnotationTool;
import com.abstyle.servlet.Servlet;
import com.abstyle.Util.XmlTool;
import lombok.SneakyThrows;

import javax.annotation.Resource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class SocketServer extends Thread {
    private ServerSocket serverSocket = null;
    private int port;

    public SocketServer() throws IOException {
        serverSocket = new ServerSocket(8080);
    }

    public SocketServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @SneakyThrows
    @Override
    public void run() {
        Socket socket = null;

        InputStream banner = Resource.class.getResourceAsStream("/banner.txt");
        byte[] bytes = new byte[512];
        int count = 0;
        while ((count = Objects.requireNonNull(banner).read(bytes)) != -1) {
            System.out.println(new String(bytes, 0, count));
        }

        System.out.println("Tomcat start successful  " + "port:" + serverSocket.getLocalPort());

        while (true) {
            try {
                assert serverSocket != null;
                socket = serverSocket.accept();

                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                Request request = new Request(inputStream);
                Response response = new Response(outputStream);

                HashSet<String> html = XmlTool.findHtml();

                if ("/".equals(request.getUrl()) && html.contains("/index.html")) {
                    XmlTool.responseHtml(outputStream, "/index.html");
                } else if (html.contains(request.getUrl())) {
                    XmlTool.responseHtml(outputStream, request.getUrl());
                } else {
                    boolean flag = false;
                    for (File file : Objects.requireNonNull(new File(Objects.requireNonNull(SocketServer.class.getClassLoader().getResource("")).getFile()).listFiles())) {
                        if ("web.xml".equals(file.getName())) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        Map<String, String> webXmlMap = XmlTool.loadWebXml();
                        if (webXmlMap.get(request.getUrl()) != null) {
                            Class<Servlet> servletClass = (Class<Servlet>) Class.forName(webXmlMap.get(request.getUrl()));
                            Servlet servlet = servletClass.newInstance();
                            servlet.service(request, response);
                        }
                    }
                    if (AnnotationTool.isExistAnnotationInApplication(TomcatApplication.className)) {
                        HashMap<String, String> annotationMap = AnnotationTool.handlerAnnotation(AnnotationTool.loadApplicationAnnotation(TomcatApplication.className));
                        if (annotationMap.get(request.getUrl()) != null) {
                            Class<Servlet> servletClass = (Class<Servlet>) Class.forName(annotationMap.get(request.getUrl()));
                            Servlet servlet = servletClass.newInstance();
                            servlet.service(request, response);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                assert socket != null;
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
