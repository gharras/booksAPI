package fes.ghm.books;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText etTitle = findViewById(R.id.etTitle);
        final EditText etAuthor = findViewById(R.id.etAuthor);
        final EditText etPublisher = findViewById(R.id.etPublisher);
        final EditText etIsbn = findViewById(R.id.etISBN);

        final Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String author = etAuthor.getText().toString().trim();
                String publisher = etPublisher.getText().toString().trim();
                String isbn = etIsbn.getText().toString().trim();

                if (title.isEmpty() && author.isEmpty() && publisher.isEmpty() && isbn.isEmpty()) {
                    String message = getString(R.string.error_data_message);
                    // Toast is the message that show up for a moment and then disappear
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                } else {
                    URL queryUrl = ApiUtils.buildUrl(title, author, publisher, isbn);
                    // sharedPreference
                    Context context = getApplicationContext();
                    int position = SpUtil.getPreferenceInt(context, SpUtil.POSITION);
                    if (position == 0 || position == 5){
                        position = 1;
                    } else {
                        position ++;
                    }
                    // let create the key we will use for our sharedPreference
                    String key = SpUtil.QUERY + String.valueOf(position);
                    String value = title + "," + author + "," + publisher + "," + isbn;

                    SpUtil.setPreferenceString(context, key, value);
                    SpUtil.setPreferenceInt(context, SpUtil.POSITION, position);

                    Intent intent = new Intent(getApplicationContext(), BookListActiviy.class);
                    intent.putExtra("Query", queryUrl.toString());
                    startActivity(intent);
                }
            }
        });
    }
}
