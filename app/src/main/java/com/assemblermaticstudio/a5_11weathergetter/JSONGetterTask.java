package com.assemblermaticstudio.a5_11weathergetter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class JSONGetterTask implements Callable<JSONObject> {

    private final String s_url;

    JSONGetterTask(String s){
        this.s_url = s;
    }

    @Override
    public JSONObject call() {

        URL url;
        HttpURLConnection httpURLConnection;
        StringBuilder stringBuilder;

        try {
            url = new URL(s_url);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.connect();
            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inReader = new InputStreamReader(in);
            stringBuilder = new StringBuilder();

            int data = inReader.read();

            while(data != -1){
                char c_current = (char) data;
                stringBuilder.append(c_current);

                data = inReader.read();
            }

            in.close();
            inReader.close();
            httpURLConnection.disconnect();

            return new JSONObject(stringBuilder.toString());

        } catch (IOException | JSONException e){
            e.printStackTrace();
        }

        return null;
    }
}
