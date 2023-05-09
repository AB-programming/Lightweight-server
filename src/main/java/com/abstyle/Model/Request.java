package com.abstyle.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    private String method;
    private String url;
    private InputStream inputStream;

    public Request(InputStream inputStream) throws IOException {
        byte[] buf = new byte[1024];
        String str = "";
        int count = 0;

        if ((count = inputStream.read(buf)) > 0) {
            str = new String(buf, 0, count);
        }

        /*
        *  Get /abc/def HTTP/1.1
        *  Host: 127.0.0.1
        * */
        String head = str.split("\n")[0];
        this.method = head.split("\\s")[0];
        this.url = head.split("\\s")[1];
    }
}
