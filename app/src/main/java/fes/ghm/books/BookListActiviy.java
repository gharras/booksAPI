package fes.ghm.books;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

/**
 * this class will be our Main activity where we will show our list of books that we will call
 * with the help of google Book API
 */

/**
 * we implements OnQueryTextListener so that we can use our searchView widget and be able
 * to do search from it
 */
public class BookListActiviy extends AppCompatActivity implements SearchView.OnQueryTextListener {

//    private TextView mTvResutlt;

    /**
     * we want that our ProgressBar show up once the application start runing
     * and once we get the data from the internet we make it invisible again
     */

    private ProgressBar mLoadingProgress;

    /**
     * our text view who will show up if there is an error while loading
     * data from the Google Book APU
     */

    private TextView mTvError;

    /**
     * now we will create our recyclerView and bind it to the recyclerView we just created in the
     * layout, and assign it on the onCreate method
     */
    RecyclerView mRvBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        /**
         * now to use out ApiUtils class we need to first build our URL
         * and we want to look for every books who has the word "Cooking" on it
         * after getting the URL and the JSON text we want to show our result in our TextView
         */
        URL bookUrl;
        String jsonResult;
//        mTvResutlt = (TextView) findViewById(R.id.tvResponse);
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
        mTvError = (TextView) findViewById(R.id.tv_error);

        mRvBooks = (RecyclerView) findViewById(R.id.rv_books);


        /**
         * in case we get called by an intent from advanced search
         */
        Intent intent = getIntent();
        String query = intent.getStringExtra("Query");


        LinearLayoutManager booksLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false
        );

        mRvBooks.setLayoutManager(booksLayoutManager);
        try {
            if(query == null || query.isEmpty()){
                bookUrl = ApiUtils.buildUrl("Cooking");
            } else {
                bookUrl = new URL(query);
            }

            new BooksQueryTask().execute(bookUrl);

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
        /*try{
            bookUrl = ApiUtils.buildUrl("Coocking");
            jsonResult = ApiUtils.getJson(bookUrl);
            mTvResutlt.setText(jsonResult);
        } catch (IOException e){
            Log.d("Error", e.getMessage());
        }*/


    }

    /**
     * we override this method to show our menu in this activity
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // we use this method to add our menu to our activity
        getMenuInflater().inflate(R.menu.book_list_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        // because the MenuItemCompact.getActionView became deprecated we will use the getActionView
        // directly from the searchItem
        //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        // we will add the ist of query we get from our shared preference
        ArrayList<String> recentList = SpUtil.getQueryList(getApplicationContext());
        int itemCount = recentList.size();
        MenuItem recentMenu;
        for (int i = 0; i < itemCount; i++){
            recentMenu = menu.add(Menu.NONE, i,Menu.NONE, recentList.get(i));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                break;
            case R.id.action_advanced_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                int position = item.getItemId() + 1;
                String preferenceName = SpUtil.QUERY + String.valueOf(position);
                String query = SpUtil.getPreferenceString(getApplicationContext(), preferenceName);
                String[] prefParams = query.split("\\,");
                String[] queryParams = new String[4];
                for (int i = 0; i < prefParams.length; i ++){
                    queryParams[i] = prefParams[i];
                }

                URL bookUrl = ApiUtils.buildUrl(
                        ((queryParams[0] == null) ? "" : queryParams[0]),
                        ((queryParams[1] == null) ? "" : queryParams[1]),
                        ((queryParams[2] == null) ? "" : queryParams[2]),
                        ((queryParams[3] == null) ? "" : queryParams[3])
                );


                new BooksQueryTask().execute(bookUrl);
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    /**
     * this method will be activated once we submit our search for a book
     *
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            URL bookUrl = ApiUtils.buildUrl(query);
            new BooksQueryTask().execute(bookUrl);
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * we will create an internal class that extends AsyncTask to use it for our background work
     */
    public class BooksQueryTask extends AsyncTask<URL, Void, String> {

        /**
         * we override onPreExecute method to make our ProgressBar visible
         */
        @Override
        protected void onPreExecute() {
            mLoadingProgress.setVisibility(View.VISIBLE);
        }

        /**
         * Because our Params type in the AsyncTask Declaration is URL
         * the param used in the "doInBackground" method is an array of URL
         * so we need to treat our data as array even if we have only one URL
         *
         * @param urls
         */
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String result = null;
            try {
                result = ApiUtils.getJson(searchUrl);
            } catch (Exception e) {
                Log.e("Eroor", e.getMessage());
            }

            return result;
        }

        /**
         * because our "doInBackGround" method work in a separate thread so our main thread
         * can't access it result
         * so for that we need to implement "onPostExecute" who receive by default the return
         * value from "doInBackGround" as Parameter
         * and "onPostExecute" run in the main thread so it can communicate with our main thread
         * and pass the data we need for it
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            mLoadingProgress.setVisibility(View.INVISIBLE);
            if (result == null) {
//                mTvResutlt.setVisibility(View.INVISIBLE);
                mRvBooks.setVisibility(View.INVISIBLE);
                mTvError.setVisibility(View.VISIBLE);
            } else {
//                mTvResutlt.setVisibility(View.VISIBLE);
                mRvBooks.setVisibility(View.VISIBLE);
                mTvError.setVisibility(View.INVISIBLE);
                ArrayList<Book> books = ApiUtils.getBooksFromJson(result);
//            String resultString = "";
//            for (Book book : books){
//                resultString += book.title + " \n"
//                        + book.publishedDate +"\n\n";
//            }

                //now we will create the Adapter that will fill our RecyclerView
                BooksAdapter adapter = new BooksAdapter(books);
                // finally we will pass our adapter to our RecyclerView by the setAdapter method
                mRvBooks.setAdapter(adapter);

            }


//            mTvResutlt.setText(resultString);
//            super.onPostExecute(s);
        }
    }
}
