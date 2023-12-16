package org.example.common.models;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class URL {

    public static String getHTTP(String urlStr){
        String result = "";

    try {
        java.net.URL url = new java.net.URL(urlStr);

        InputStream is = url.openStream();
        InputStreamReader isr = new InputStreamReader(is);
        int i = isr.read();

        while(i != -1) {
            result += (char) i;
            i = isr.read();
        }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
        throw new RuntimeException(e);
    }

        return result;
    }

    public static File getFile(String urlStr, String filePath) throws Exception {
        File result = new File("");

        java.net.URL url = new java.net.URL(urlStr);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setAllowUserInteraction(false);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestMethod("GET");

        conn.connect();

        int httpStatusCode = conn.getResponseCode();
        if (httpStatusCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP Status " + httpStatusCode);
        }

        String contentType = conn.getContentType();
        System.out.println("Content-Type: " + contentType);

        // Input Stream
        DataInputStream dataInStream = new DataInputStream(
                conn.getInputStream());

        // Output Stream
        DataOutputStream dataOutStream = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(filePath)));

        // Read Data
        byte[] b = new byte[4096];
        int readByte = 0;

        while (-1 != (readByte = dataInStream.read(b))) {
            dataOutStream.write(b, 0, readByte);
        }

        // Close Stream
        dataInStream.close();
        dataOutStream.close();

        return result;
    }
}
