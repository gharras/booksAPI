package fes.ghm.books;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * this class will only have statics method and constants
 * we will only use it to get access to the google Books api
 */

public class ApiUtils {

    /**
     * the Base URL that doesn't change when we want to look for books
     */
    public static final String BASE_API_URL =
            "https://www.googleapis.com/books/v1/volumes";

    /**
     * we will create the key that we will use in our URI to look for the right title
     */
    public static final String QUERY_PARAMETER_KEY = "q";

    /**
     * if in the future we need to deploy our app
     * google state in the google conditions that each application need to identify itself
     * look for google api developer dashboard
     * there you need to have a google account
     * and when you go to credentials and create a key api for you app to use it when you want
     * want to use google apis
     * for that we will create 2 other constant
     * one is the KEY we will call it "key"
     * the other is the actual key that was generated for us and this key must be confidential
     * and we will add this key when we build our URI by the method "appendQueryParameter"
     * but only after the app is published because the app need to be registered first so that
     * they can know about the key
     * this is a registered key from the owner of the course : AIzaSyCK9Z5fuQBLM0RXV58u1Wmkt9zznb0269c
     * but unless you publish it you can bypass it and not use the key
     */
    public static final String KEY = "key";
    public static final String API_KEY = "AIzaSyCK9Z5fuQBLM0RXV58u1Wmkt9zznb0269c";


    /**
     * we will create the Constant we will use in our Advance search activity
     */
    public static final String TITLE = "intitle:";
    public static final String AUTHOR = "inauthor:";
    public static final String PUBLISHER = "inpublisher:";
    public static final String ISBN = "isbn:";

    /**
     * a private constructor so that we don't instantiate this Utils class
     */
    private ApiUtils() {
    }

    /**
     * we give the title of the book we look for
     * and we return the URL corresponding that have a list of all books that might have being
     * the answer for our request
     * for best practice rather than using a String to build our URL
     * we will use a URI builder Uri from android.net package
     *
     * @param title
     * @return
     */
    public static URL buildUrl(String title) {
//        String  fullUrl = BASE_API_URL + "?q=" + title;
        URL url = null;

        // to convert our string to a URI we use the pare method from the Uri class
        Uri uri = Uri.parse(BASE_API_URL)
                .buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, title)
                .appendQueryParameter(KEY, API_KEY)
                .build();
        try {
//            url = new URL(fullUrl);
            url = new URL(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }


    public static URL buildUrl(String title, String author, String publisher, String isbn) {
        URL url = null;
        StringBuilder sb = new StringBuilder();
        if(!title.isEmpty()) sb.append(TITLE + title + "+");
        if(!author.isEmpty()) sb.append(AUTHOR + author + "+");
        if(!publisher.isEmpty()) sb.append(PUBLISHER + publisher + "+");
        if(!isbn.isEmpty()) sb.append(ISBN + isbn + "+");
        if(sb.length() > 0)
            sb.setLength(sb.length() - 1);

        Uri uri = Uri.parse(BASE_API_URL)
                .buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, sb.toString())
                .appendQueryParameter(KEY, API_KEY)
                .build();
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * using the URL we give as parameter we get the answer to our request as a String
     * having the JSON file text
     * <p>
     * because this function communicate with the internet so we must do our work on
     * the background otherwise we will get a "NetworkOnMainThreadException"
     * this Exception means that the main thread (UI Thread) doesn't allow the user
     * to use Network connection in the UI Thread so we will need to use an "AsyncTask"
     * to do this Job in background and then send back result to our main Thread to not
     * get an Exception
     *
     * @param url
     * @return
     */
    public static String getJson(URL url) throws IOException {

        /**
         *  the HttpURLConnection is the class used to connect with our google books api
         *  Note: URL connection instance doesn't establish the actual network connection
         *  this will happen only when we try to read data
         *  to open the connection we use the "openConnection" method on the URL
         */
       /* try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        } catch ( Exception e ) {
            e.printStackTrace();
        }*/

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // Added the key at a requestProperty from my connection because it return null
        // if I use it in the URL
//        connection.addRequestProperty(KEY, API_KEY);
        String dataToReturn = null;

        try {
            /**
             * InputStream is a very flexible class that allow us to read any page
             * so we will use our connection and use the method "getInputStream'
             */
            InputStream stream = connection.getInputStream();

            /**
             * because we are reading a JSON file here, so we will convert our InputStream to String
             * there is a lot of way to do that on Android
             * one of them is by using a Scanner which has the advantage of buffering the data
             * and encoding the character to UTF16 which is the Android Format
             */
            Scanner scanner = new Scanner(stream);

            /**
             * the scanner can be used to delimit large pieces of stream into smaller one
             * in this case we will set the delimiter  to "\\A"
             * it's means that we want to read everything
             */
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();

            /**
             * if there is data we return it; otherwise we return null
             */
            dataToReturn = hasData ? scanner.next() : null;
            return dataToReturn;
        } catch (Exception e) {
            Log.d("Eroor", e.toString());
            return null;
        } finally {
            /**
             * we need to disconnect the connection at the end so to not have problem later
             */
            connection.disconnect();
        }
    }


    /**
     * we will now create a method that will return an Array of Books from our JSON file
     */

    public static ArrayList<Book> getBooksFromJson(String json) {
        final String ID = "id";
        final String TITLE = "title";
        final String SUB_TITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final String VOLUME_INFO = "volumeInfo";
        final String DESCRIPTION = "description";
        final String IMAGE_LINKS = "imageLinks";
        final String THUMBNAIL = "thumbnail";

        ArrayList<Book> books = new ArrayList<>();
        try {
            // first convert our String to a JSONObject so that we can parse it
            JSONObject jsonBooks = new JSONObject(json);
            // we get the array that contain all the books by passing the name of the Array
            // in our case is "items"
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);

            // get the number of books in our  JSONArray
            int numberOfBooks = arrayBooks.length();

            // we will now loop our arrayBooks to create each JSONObject alone and add it
            // to our Books array


            for (int i = 0; i < numberOfBooks; i++) {
                JSONObject bookJson = arrayBooks.getJSONObject(i);
                // get the VolumeInfo from our JSON Object where we have the ttite, authors etc
                JSONObject volumeInfoJson = bookJson.getJSONObject(VOLUME_INFO);
                JSONObject imageLinksJson = null;
                if(volumeInfoJson.has(IMAGE_LINKS))
                    imageLinksJson = volumeInfoJson.getJSONObject(IMAGE_LINKS);
                //if we want any data now we call it by calling the appropriate method with the
                // appropriate key , and we must be sure to iterate if our data is an arrau
                int numOfAuthor;
                try {
                    numOfAuthor = volumeInfoJson.getJSONArray(AUTHORS).length();
                } catch (Exception e){
                    numOfAuthor = 0;
                }
                String[] authors = new String[numOfAuthor];
                for (int j = 0; j < numOfAuthor; j++) {
                    authors[j] = volumeInfoJson.getJSONArray(AUTHORS).get(j).toString();
                }

                Book book = new Book(
                        bookJson.getString(ID),
                        volumeInfoJson.getString(TITLE),
                        (volumeInfoJson.isNull(SUB_TITLE) ? "" : volumeInfoJson.getString(SUB_TITLE)),
                        authors,
                        (volumeInfoJson.isNull(PUBLISHER) ? "" : volumeInfoJson.getString(PUBLISHER)),
                        (volumeInfoJson.isNull(PUBLISHED_DATE) ? "" : volumeInfoJson.getString(PUBLISHED_DATE)),
                        (volumeInfoJson.isNull(DESCRIPTION) ? "" : volumeInfoJson.getString(DESCRIPTION)),
                        (imageLinksJson==null ? "" : imageLinksJson.getString(THUMBNAIL))
                );
                books.add(book);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }
}
