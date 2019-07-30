package fes.ghm.books;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import fes.ghm.books.databinding.ActivityBookDetailBinding;

public class BookDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Book book = null;
        // because our activity is called from another activity by an intent we will use that intent
        // to get our book using the method getIntent().getParcelableExtra because it's an object
        book = getIntent().getParcelableExtra("Book");

        // to be able to activate dataBing we need to activate it in the activity
        // we have a class called ActivityBookDetailBinding that was created automatically
        // thanks to Android Studio so we will use it to bind our data which is the name of our
        // layout file converted to pascal case + the Binding suffix

        // next we use the DataBindingUtil class and call the method setContentView
        // we pass the activity and the layout file to it
        ActivityBookDetailBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_book_detail);

        // finally we will pass the current book to the object binding that we created
        binding.setBook(book);
    }
}
