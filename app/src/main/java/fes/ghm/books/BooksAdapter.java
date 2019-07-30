package fes.ghm.books;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * this class will play the role of the Adapter of our recycle view
 * we will Extends the RecycleView.Adapter
 * when we extends this class we will need to override 3 methods
 * onCreateViewHolder, onBindViewHolder, getItemCount
 */

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    ArrayList<Book> mBooks;

    public BooksAdapter(ArrayList<Book> books) {
        this.mBooks = books;
    }

    /**
     * this method is called when our recyclerView need a new ViewHolder
     * in this method we need to "inflate" the row layout that we have defined in book_list_item
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // we get the context of our parent View
        Context context = viewGroup.getContext();
        // new we will use the "LayoutInflater.from()" and pass the context as parameter to from method
        // then we call the inflate method and passing our layout as a resource for our itemView
        // as first parameter, the second parameter is the root we will pass the parent,
        // the third parameter we use false to not attack it to the root
        View itemView = LayoutInflater.from(context).inflate(R.layout.book_list_item,
                viewGroup,
                false);
        // last we pass a new BookViewHolder while passing our itemView as parameter
        return new BookViewHolder(itemView);
    }


    /**
     * this method is called to display the data
     * first we get the book at the specific position and bind it to the holder
     *
     * @param bookViewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder bookViewHolder, int position) {
        Book book = mBooks.get(position);
        bookViewHolder.bind(book);
    }

    /**
     * in this method we will return the size of the books arrays we have
     * so that the recyclerView can know the size of the items we have
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    /**
     * first we wil create  a public class called BookViewHolder that extends the ViewHolder class
     */

    /**
     * to be able to select our item separatly we will add a click event on each item
     *
     */
    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle;
        TextView tvAuthors;
        TextView tvDate;
        TextView tvPublisher;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvAuthors = (TextView) itemView.findViewById(R.id.tvAuthors);
            tvPublisher = (TextView) itemView.findViewById(R.id.tvPublisher);
            tvDate = (TextView) itemView.findViewById(R.id.tvPublishedDate);
            itemView.setOnClickListener(this);
        }

        /**
         * we will use this method to put our books data in the text view we have defined
         *
         * @param book
         */
        public void bind(Book book) {
            tvTitle.setText(book.title);
            /**
             * because Authors is an array we will need to use a string to contain all the authors
             */
//            String authors = "";
//            int i = 0;
//            for (String author : book.authors) {
//                author += author;
//                i++;
//                if (i < book.authors.length)
//                    authors += ", ";
//            }
            tvAuthors.setText(book.authors);
            tvDate.setText(book.publishedDate);
            tvPublisher.setText(book.publisher);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Book selectedBook = mBooks.get(position);
            // we put tin the intent the context of the view selected and the activity we want to start
            Intent intent = new Intent(v.getContext(), BookDetail.class);
            // put the data we want to pass to the BookDetal activity
            intent.putExtra("Book", selectedBook);
            // start the activity
            v.getContext().startActivity(intent);
        }
    }
}
