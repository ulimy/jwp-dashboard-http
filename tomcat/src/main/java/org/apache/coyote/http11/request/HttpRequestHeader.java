package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.common.HttpCookie;

public class HttpRequestHeader {

    private static final String KEY_OF_COOKIE = "Cookie";
    private static final String DELIMITER_OF_HEADER = ": ";

    private final Map<String, String> headers;
    private final HttpCookie cookie;

    private HttpRequestHeader(Map<String, String> headers, HttpCookie cookie) {
        this.headers = headers;
        this.cookie = cookie;
    }

    public static HttpRequestHeader from(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String header = bufferedReader.readLine();
        while (!header.isBlank() && !header.isEmpty()) {
            String[] parsedHeader = header.split(DELIMITER_OF_HEADER);
            headers.put(parsedHeader[0], parsedHeader[1]);
            header = bufferedReader.readLine();
        }

        if (headers.containsKey(KEY_OF_COOKIE)) {
            HttpCookie cookie = HttpCookie.createByParsing(headers.get(KEY_OF_COOKIE));
            headers.remove(KEY_OF_COOKIE);

            return new HttpRequestHeader(headers, cookie);
        }

        return new HttpRequestHeader(headers, HttpCookie.empty());
    }

    public String get(String key) {
        return headers.get(key);
    }

    public boolean containsSession() {
        return cookie.containsSession();
    }

    public String getSession() {
        return cookie.getSession();
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }
}
