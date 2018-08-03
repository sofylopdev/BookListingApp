package com.example.sofia.booklisting;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sofia on 10/7/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public static final String LOG_TAG = BookAdapter.class.getName();
    private ImageView thumbnail;
    private TextView title;
    private TextView authors;
    private TextView publisher;
    private TextView publishedDate;
    private TextView description;
    private Book currentBook;

    public BookAdapter(@NonNull Context context, @LayoutRes int resource, List<Book> bookList) {
        super(context, 0, bookList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_book, parent, false);
        }

        title = listItemView.findViewById(R.id.title);
        authors = listItemView.findViewById(R.id.authors);
        publisher = listItemView.findViewById(R.id.publisher);
        publishedDate = listItemView.findViewById(R.id.publishedDate);
        description = listItemView.findViewById(R.id.description);
        thumbnail = listItemView.findViewById(R.id.thumbnail);
        currentBook = getItem(position);

        updateUI();

        return listItemView;
    }

    private void updateUI() {
        if (currentBook != null) {
            thumbnail.setImageBitmap(currentBook.getImageThumb());
            title.setText(currentBook.getTitle());
            authors.setText(currentBook.getAuthors().toString());
            publisher.setText(currentBook.getPublisher());
            publishedDate.setText(currentBook.getPublishedDate());
            description.setText(currentBook.getDescription());

        } else {
            Log.d(LOG_TAG, "current book is null: ");
        }
    }
}
