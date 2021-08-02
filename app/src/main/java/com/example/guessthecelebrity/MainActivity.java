package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    // widgets
    private ImageView mCelebrityImages;

    // variables
    private ArrayList<String> arrayListURLs, arrayListNames;
    private int randomCelebrity;

    public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();

                return BitmapFactory.decodeStream(inputStream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

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

        arrayListURLs = new ArrayList<>();
        arrayListNames = new ArrayList<>();

        mCelebrityImages = findViewById(R.id.ivCelebrity);

        PageSourceTask pageSourceTask = new PageSourceTask();
        String result;

        try {
            result = pageSourceTask.execute("https://www.imdb.com/list/ls052283250/").get();

            // <div class="aux-content-widget-2" id="feedback-widget">

            String[] splitResult = result
                    .split("<div class=\"aux-content-widget-2\" id=\"feedback-widget\">");

            Pattern pattern = Pattern.compile("src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(splitResult[0]);
            int counter = 0;

            while (matcher.find()) {

                counter++;

                if (counter > 5) {
                    arrayListURLs.add(matcher.group(1));
                }
            }

            Pattern pattern1 = Pattern.compile("alt=\"(.*?)\"");
            Matcher matcher1 = pattern1.matcher(splitResult[0]);

            while (matcher1.find()) {
                arrayListNames.add(matcher.group(1));
            }

            Random generateRandomInstances = new Random();
            randomCelebrity = generateRandomInstances.nextInt(arrayListURLs.size());

            Bitmap imgCelebrity;

            ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask();
            imgCelebrity = imageDownloaderTask.execute(arrayListURLs.get(randomCelebrity)).get();

            mCelebrityImages.setImageBitmap(imgCelebrity);

            String celebrityName;



        } catch (ExecutionException | InterruptedException e) {

            e.printStackTrace();
        }
    }
}