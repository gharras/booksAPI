package fes.ghm.books;

/**
 * this Book class will represent 1 item that we extract from the JSON file
 */

import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * to be able to pass our Book object between activities thanks to the intent
 * we will first have to implement the parcelable interface and it's method
 */


public class Book implements Parcelable {

    public String id;
    public String title;
    public String subTitle;
    //    public String[] authors;
    // to simplify our work we will use String rather than String [] for authors
    public String authors;
    public String publisher;
    public String publishedDate;
    public String description;
    public String thumbnail;

    public Book(String id, String title, String subTitle, String[] authors,
                String publisher, String publishedDate, String description, String thumbnail) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        // we will use TextUtils.join method to convert our authors array to a string with
        // a ", " as delimiter
        this.authors = TextUtils.join(", ", authors);
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    protected Book(Parcel in) {
        id = in.readString();
        title = in.readString();
        subTitle = in.readString();
        authors = in.readString();
        publisher = in.readString();
        publishedDate = in.readString();
        description = in.readString();
        thumbnail = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(authors);
        dest.writeString(publisher);
        dest.writeString(publishedDate);
        dest.writeString(description);
        dest.writeString(thumbnail);
    }

    /**
     * now we will create our Binding Adapter so that we can bind the image with the
     * imageView
     */

    @BindingAdapter({"android:imageUrl"})
    public static void loadImage(ImageView imageView, String imageUrl) {
        /**
         * for books who don't contain image we don't want to use Picasso
         */

        if (!imageUrl.isEmpty())
            Picasso.get().load(imageUrl).
                    placeholder(R.drawable.book_open).
                    into(imageView);
        else
            imageView.setBackgroundResource(R.drawable.book_open);
    }
}
