package com.abstyle.Util;

import cn.hutool.core.util.XmlUtil;
import com.abstyle.annotation.ScanServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

public class XmlTool {

    private static final HashSet<String> htmlSet = new HashSet<>();

    public static Map<String, String> loadWebXml() {
        HashMap<String, String> result = new HashMap<>();
        Document document = XmlUtil.readXML("web.xml");
        Element element = document.getDocumentElement();
        List<Element> servletList = XmlUtil.getElements(element, "servlet");
        List<Element> servletMappingList = XmlUtil.getElements(element, "servlet-mapping");

        HashMap<String, String> servletMap = new HashMap<>();
        HashMap<String, String> servletMappingMap = new HashMap<>();

        for (Element servlet : servletList) {
            String servletName = XmlUtil.getElement(servlet, "servlet-name").getTextContent();
            String servletClass = XmlUtil.getElement(servlet, "servlet-class").getTextContent();
            if (!"".equals(servletName) && !"".equals(servletClass)) {
                servletMap.put(servletName, servletClass);
            }
        }

        for (Element servletMapping : servletMappingList) {
            String servletName = XmlUtil.getElement(servletMapping, "servlet-name").getTextContent();
            String urlPattern = XmlUtil.getElement(servletMapping, "url-pattern").getTextContent();
            if (!"".equals(servletName) && !"".equals(urlPattern)) {
                servletMappingMap.put(servletName, urlPattern);
            }
        }

        for (Map.Entry<String, String> servletMapping : servletMappingMap.entrySet()) {
            if (servletMap.get(servletMapping.getKey()) != null) {
                result.put(servletMapping.getValue(), servletMap.get(servletMapping.getKey()));
            }
        }
        return result;
    }

    public static void loadResources(File[] files, String filesPath) {
        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if ("html".equals(fileName.substring(fileName.length() - 4))) {
                    htmlSet.add(filesPath + fileName);
                }
            }
            if (file.isDirectory()) {
                loadResources(Objects.requireNonNull(file.listFiles()), filesPath + file.getName() + '/');
            }
        }
    }

    public static HashSet<String> findHtml() {
        String resource = Objects.requireNonNull(XmlTool.class.getClassLoader().getResource("")).getFile();
        File path = new File(resource);
        File[] files = path.listFiles();
        loadResources(Objects.requireNonNull(files), "/");
        return htmlSet;
    }

    public static void responseHtml(OutputStream outputStream, String file) throws IOException {
        InputStream resourceAsStream = Resource.class.getResourceAsStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(resourceAsStream));
        byte[] bytes = new byte[1024];
        int count = 0;
        outputStream.write("HTTP/1.1 200\nContent-Type: text/html\n\r\n".getBytes());
        while ((count = bufferedInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, count);
        }
        outputStream.flush();
        outputStream.close();
    }
}


