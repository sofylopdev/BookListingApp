package com.example.sofia.booklisting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sofia on 10/7/2017.
 */

public class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    public static final int ReadTimeout = 10000;
    public static final int ConnectTimeout = 15000;

    private QueryUtils() {
    }

    public static List<Book> extractBooks(String requestUrl) {

        URL completeUrl = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(completeUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }


        List<Book> foundBooks = fetchBooksData(jsonResponse);
        // Return the list of books
        return foundBooks;
    }


    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL completeUrl) throws IOException {
        String jsonResponse = "";

        if (completeUrl == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) completeUrl.openConnection();
            urlConnection.setReadTimeout(ReadTimeout /* milliseconds */);
            urlConnection.setConnectTimeout(ConnectTimeout /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Respose code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving book results (bad connection?).", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader buffer = new BufferedReader(reader);
            String line = buffer.readLine();
            while (line != null) {
                output.append(line);
                line = buffer.readLine();
            }

        }
        return output.toString();
    }

    private static List<Book> fetchBooksData(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return Collections.emptyList();
        }

        List<Book> booksFound = new ArrayList<Book>();

        try {
            JSONObject resultadosProcura = new JSONObject(jsonResponse);
            JSONArray livrosArray = resultadosProcura.getJSONArray("items");
            if (livrosArray.length() > 0) {
                for (int i = 0; i < livrosArray.length(); i++) {
                    JSONObject currentBook = livrosArray.getJSONObject(i);
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    String title = volumeInfo.getString("title");

                    List<String> authorsList = new ArrayList<>();
                    try {
                        JSONArray authors = volumeInfo.getJSONArray("authors");
                        if (authors.length() > 0) {
                            for (int j = 0; j < authors.length(); j++) {
                                authorsList.add(authors.getString(j));
                            }
                        }
                    } catch (JSONException e) {
                        authorsList.add("No authors found.");
                    }

                    String publisher;
                    try {
                        publisher = volumeInfo.getString("publisher");
                    } catch (JSONException e) {
                        publisher = "No publisher found.";
                    }

                    String publishedDate;
                    try {
                        publishedDate = volumeInfo.getString("publishedDate");
                    } catch (JSONException e) {
                        publishedDate = "No published date found.";
                    }

                    String description;
                    try {
                        description = volumeInfo.getString("description");
                    } catch (JSONException e) {
                        description = "No description found.";
                    }

                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    String imageThumb = imageLinks.getString("smallThumbnail");
                    URL imageUrl = createUrl(imageThumb);
                    Bitmap bitmap = fechThumbnail(imageUrl);

                    String url = "";
                    try {
                        url = volumeInfo.getString("infoLink");
                    } catch (JSONException e) {
                        description = "No link found.";
                    }

                    Book createBook = new Book(title, authorsList, publisher, publishedDate, description, bitmap, url);
                    booksFound.add(createBook);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }

        return booksFound;
    }

    private static Bitmap fechThumbnail(URL url) {
        Bitmap bitmap = null;
        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(ReadTimeout /* milliseconds */);
            urlConnection.setConnectTimeout(ConnectTimeout /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Respose code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving book results (bad connection?).", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Could't close input stream");
                }
            }
        }
        return bitmap;
    }
}
