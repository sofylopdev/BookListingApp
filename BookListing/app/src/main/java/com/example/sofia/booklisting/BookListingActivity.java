package com.example.sofia.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookListingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String LOG_TAG = BookListingActivity.class.getName();

    private static final String GOOGLE_BOOKS_API_base_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static String userInput = "";
    //todo: Insert key here:
    private static String key = "Insert_key_here";

    private String final_request;

    private BookAdapter mAdapter;
    private EditText searchWord;
    private TextView emptyState;

    private static int BOOKS_LOADER_ID = 1;
    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_listing);

        searchWord = (EditText) findViewById(R.id.wordSearch);
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchWord.getWindowToken(), 0);

        ListView booksListView = (ListView) findViewById(R.id.listOfBooks);
        emptyState = (TextView) findViewById(R.id.emptyState);
        booksListView.setEmptyView(emptyState);

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mAdapter = new BookAdapter(this, 0, new ArrayList<Book>());
        booksListView.setAdapter(mAdapter);

        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book currentBook = mAdapter.getItem(i);
                try {
                    Uri bookUri = Uri.parse(currentBook.getUrl());

                    Intent goToGoogleBooks = new Intent(Intent.ACTION_VIEW, bookUri);
                    startActivity(goToGoogleBooks);
                } catch (NullPointerException e) {
                    Log.d(LOG_TAG, "Problem with uri String: " + e);
                }
            }
        });

        if (savedInstanceState != null) {
            final_request = savedInstanceState.getString("BooksUrl");
            Log.d(LOG_TAG, "SavedInstanceState not null :" + final_request);
            loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(BOOKS_LOADER_ID, null, this);

        } else {
            final_request = null;
            Log.d(LOG_TAG, "NO SAVED INSTANCE");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("BooksUrl", final_request);
    }

    public void search(View v) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        if (!searchWord.getText().toString().isEmpty()) {
            emptyState.setVisibility(View.INVISIBLE);
            userInput = searchWord.getText().toString();

            final_request = GOOGLE_BOOKS_API_base_REQUEST_URL + userInput + key;
            Log.d(LOG_TAG, "final request: " + final_request);

            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                mAdapter.clear();
                emptyState.setText("");
                getSupportLoaderManager().initLoader(BOOKS_LOADER_ID, null, this);
                mAdapter.notifyDataSetChanged();

            } else {
                loadingIndicator.setVisibility(View.GONE);
                Log.d(LOG_TAG, "no internet");
                mAdapter.clear();
                emptyState.setVisibility(View.VISIBLE);
                emptyState.setText("No Internet connection.");
            }

        } else {
            Log.d(LOG_TAG, "No User Input.");
            loadingIndicator.setVisibility(View.GONE);
        }
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchWord.getWindowToken(), 0);

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "on CREATE Loader");
        return new GetBooksLoader(this, final_request);
    }


    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        if (searchWord.length() > 0) {
            searchWord.getText().clear();
        }
        Log.d(LOG_TAG, "on Load FINISHED");
        if (books != null && !books.isEmpty()) {
            Log.d(LOG_TAG, "books list: " + books);
            mAdapter.addAll(books);
        }
        emptyState.setText(" :( No books found.");
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
        Log.d(LOG_TAG, "on Loader RESET");
    }


    ////////////////////////////////////////////////////////////////////////inner class:
    private static class GetBooksLoader extends AsyncTaskLoader<List<Book>> {

        private String url;

        private GetBooksLoader(Context context, String url) {
            super(context);
            this.url = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public List<Book> loadInBackground() {
            if (url == null) {
                return null;
            }
            return QueryUtils.extractBooks(url);
        }
    }
}
