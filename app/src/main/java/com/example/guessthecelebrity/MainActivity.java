package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public class PageSourceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection httpURLConnection;
            String result = "";

            try {

                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();
                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }

                return result;

            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PageSourceTask pageSourceTask = new PageSourceTask();
        String result;

        try {
            result = pageSourceTask.execute("https://www.imdb.com/list/ls052283250/").get();

            // <div class="aux-content-widget-2" id="feedback-widget">

            String[] splitResult = result
                    .split("<div class=\"aux-content-widget-2\" id=\"feedback-widget\">");

            Pattern pattern = Pattern.compile("src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(splitResult[0]);

            while (matcher.find()) {
                Log.i("URL Content", "" + matcher.group(1));
            }

        } catch (ExecutionException | InterruptedException e) {

            e.printStackTrace();
        }
    }
}