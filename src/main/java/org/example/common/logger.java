package org.example.common;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class logger {

    private static final String FILE_PATH ="O:\\9999.Ohter\\99901.log\\99101.discord\\";

    public static void info(String file_name, String channnelName, String msg){
        try{
            File serverFilePath = new File(FILE_PATH + file_name);
            if(!serverFilePath.exists()){
                serverFilePath.mkdirs();
            }
            File file = new File(FILE_PATH + file_name + "\\" + channnelName + ".log");
            FileWriter filewriter = new FileWriter(file, true);
            filewriter.append(msg + "\n");

            filewriter.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }


    public static boolean downloadFile(String path, String serverName, String file_name){

        boolean result = false;
        try {
            File file = new File(FILE_PATH + "img\\" + serverName);
            if(!file.exists()){
                file.mkdirs();
            }

            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");

            conn.connect();

            int httpStatusCode = conn.getResponseCode();
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP Status " + httpStatusCode);
            }

            // Input Stream
            DataInputStream dataInStream = new DataInputStream(
                    conn.getInputStream());

            // Output Stream
            DataOutputStream dataOutStream = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(FILE_PATH + "img\\" + serverName + "\\"+ file_name)));

            // Read Data
            byte[] b = new byte[4096];
            int readByte = 0;

            while (-1 != (readByte = dataInStream.read(b))) {
                dataOutStream.write(b, 0, readByte);
            }

            // Close Stream
            dataInStream.close();
            dataOutStream.close();
            result = true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
