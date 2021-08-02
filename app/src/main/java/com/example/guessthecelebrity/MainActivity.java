package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
    private Button mCelebrityNameButton, mCelebrityNameButton1,
            mCelebrityNameButton2, mCelebrityNameButton3;

    // variables
    private ArrayList<String> arrayListURLs, arrayListNames;
    private int randomCelebrity = 0;
    private int correctAnswerLocation = 0;
    private String[] celebrityNamesArray = new String[4];

    public void createQuestion() {

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

            correctAnswerLocation = generateRandomInstances.nextInt(4);
            int incorrectAnswerLocation;

            for (int i = 0; i < 4; i++) {

                if (correctAnswerLocation == i) {
                    celebrityNamesArray[i] = arrayListNames.get(randomCelebrity);
                    ;

                } else {

                    incorrectAnswerLocation = generateRandomInstances
                            .nextInt(arrayListURLs.size());
                    while (incorrectAnswerLocation == randomCelebrity) {

                        incorrectAnswerLocation = generateRandomInstances.nextInt(arrayListURLs.size());
                    }

                    celebrityNamesArray[i] = arrayListNames.get(incorrectAnswerLocation);
                }
            }

            mCelebrityNameButton.setText(celebrityNamesArray[0]);
            mCelebrityNameButton1.setText(celebrityNamesArray[1]);
            mCelebrityNameButton2.setText(celebrityNamesArray[2]);
            mCelebrityNameButton3.setText(celebrityNamesArray[3]);


        } catch (ExecutionException | InterruptedException e) {

            e.printStackTrace();
        }
    }

    public void onCelebrityNameClick(View view) {

        if (view.getTag().toString().equals(Integer.toString(correctAnswerLocation))) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong! It was "
                    + celebrityNamesArray[correctAnswerLocation], Toast.LENGTH_SHORT).show();
        }

        createQuestion();
    }

    public static class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();

                return BitmapFactory.decodeStream(inputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public static class PageSourceTask extends AsyncTask<String, Void, String> {

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
        mCelebrityNameButton = findViewById(R.id.btn);
        mCelebrityNameButton1 = findViewById(R.id.btn1);
        mCelebrityNameButton2 = findViewById(R.id.btn2);
        mCelebrityNameButton3 = findViewById(R.id.btn3);

        createQuestion();
    }
}