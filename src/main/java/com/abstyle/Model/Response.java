package com.abstyle.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;

import static java.lang.System.exit;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private OutputStream outputStream;

    public void write(String content) {
        String responseContent = "HTTP/1.1 200\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html>" +
                "<head><meta charset='UTF-8'><head/>" +
                "<body>" +
                content +
                "</body>" +
                "</html>";
        try {
            outputStream.write(responseContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            exit(0);
        }
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            exit(0);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            exit(0);
        }
    }
}
