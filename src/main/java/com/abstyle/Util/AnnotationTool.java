package com.abstyle.Util;

import com.abstyle.annotation.ScanServlet;
import com.abstyle.annotation.WebServlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AnnotationTool {
    public static String[] loadApplicationAnnotation(String applicationPackage) {
        String[] result = {};
        try {
            Class<?> aClass = Class.forName(applicationPackage);
            if (aClass.isAnnotationPresent(ScanServlet.class)) {
                ScanServlet scanServlet = aClass.getAnnotation(ScanServlet.class);
                return scanServlet.basePackage();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static HashMap<String, String> handlerAnnotation(String[] servletPackages) throws ClassNotFoundException, IOException {

        HashMap<String, String> result = new HashMap<>();

        ArrayList<String> servletList = new ArrayList<>();
        for (String servletPackage : servletPackages) {
            servletPackage = servletPackage.replace('.', '/');
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(servletPackage);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                if ("file".equals(url.getProtocol())) {
                    File file = new File(url.getFile());
                    if (file.isDirectory()) {
                        for (File servlet : Objects.requireNonNull(file.listFiles())) {
                            if ("class".equals(servlet.toString().substring(servlet.toString().length() - 5))) {
                                String[] split = servlet.toString().split("/");
                                servletList.add(servletPackage.replace("/", ".") + "." +
                                        split[split.length - 1].substring(0, split[split.length - 1].length() - 6));
                            }
                        }
                    }
                }
            }
        }

        for (String servlet : servletList) {
            Class<?> aClass = Class.forName(servlet);
            if (aClass.isAnnotationPresent(WebServlet.class)) {
                WebServlet webServlet = aClass.getAnnotation(WebServlet.class);
                String[] urlPatterns = webServlet.urlPatterns();
                for (String urlPattern : urlPatterns) {
                    result.put(urlPattern, servlet);
                }
            }
        }
        return result;
    }

    public static boolean isExistAnnotationInApplication(String applicationPackage) throws ClassNotFoundException {
        return Class.forName(applicationPackage).isAnnotationPresent(ScanServlet.class);
    }
}
