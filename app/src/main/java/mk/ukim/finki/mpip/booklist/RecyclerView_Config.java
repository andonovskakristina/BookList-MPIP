package mk.ukim.finki.mpip.booklist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RecyclerView_Config {
    private Context mContext;
    private BooksAdapter mBooksAdapter;
    private FavouritesAdapter mFavouritesAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Book> books, List<String> keys,
                          List<Book> favourites, List<String> favouritesKeys) {
        mContext = context;
        mBooksAdapter = new BooksAdapter(books, keys, favourites, favouritesKeys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mBooksAdapter);
    }

    public void setFavouritesConfig(RecyclerView recyclerView, Context context, List<Book> books, List<String> keys,
                          List<Book> favourites, List<String> favouritesKeys) {
        mContext = context;
        mFavouritesAdapter = new FavouritesAdapter(books, keys, favourites, favouritesKeys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mFavouritesAdapter);
    }

    class BookItemView extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mAuthor;
        private ImageView mThumbnail;
        private RatingBar mRating;

        private String key;

        public BookItemView(ViewGroup parent) {
            super(LayoutInflater.from(mContext).inflate(R.layout.book_list_item, parent, false));

            mTitle = (TextView) itemView.findViewById(R.id.txtBookTitle);
            mAuthor = (TextView) itemView.findViewById(R.id.txtBookAuthor);
            mThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            mRating = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }

        public void bind(Book book, String key) {
            mTitle.setText(book.getTitle());
            mAuthor.setText(book.getAuthor());
            mRating.setRating((float) book.getRating());

            new DownloadImageTask((ImageView) itemView.findViewById(R.id.thumbnail))
                    .execute(book.getThumbnail());

            this.key = key;
        }
    }

    class BooksAdapter extends RecyclerView.Adapter<BookItemView> implements Filterable {
        private List<Book> mBookList;
        private List<String> mKeys;
        private List<Book> mFavourites;
        private List<String> mFavouritesKeys;

        private List<Book> mBookListFull;
        List<String> mKeysFull;

        public BooksAdapter(List<Book> mBookList, List<String> mKeys, List<Book> mFavourites, List<String> mFavouritesKeys) {
            this.mBookList = mBookList;
            this.mKeys = mKeys;
            this.mFavourites = mFavourites;
            this.mFavouritesKeys = mFavouritesKeys;
            mBookListFull = new ArrayList<>(mBookList);
            mKeysFull = new ArrayList<>(mKeys);
        }

        @NonNull
        @Override
        public BookItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookItemView holder, final int position) {
            holder.bind(mBookList.get(position), mKeys.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BookDetails.class);
                    intent.putExtra("key", mKeys.get(position));
                    intent.putExtra("title", mBookList.get(position).getTitle());
                    intent.putExtra("author", mBookList.get(position).getAuthor());
                    intent.putExtra("isbn", mBookList.get(position).getIsbn());
                    intent.putExtra("numberPages", mBookList.get(position).getNumberPages());
                    intent.putExtra("rating", mBookList.get(position).getRating());
                    intent.putExtra("publicationDate", mBookList.get(position).getPublicationDate());
                    intent.putExtra("description", mBookList.get(position).getDescription());
                    intent.putExtra("genres", mBookList.get(position).getGenres());
                    intent.putExtra("thumbnail", mBookList.get(position).getThumbnail());
                    intent.putExtra("isFave", mFavourites.contains(mBookList.get(position)));
                    mContext.startActivity(intent);
                }
            });

            final Button mBtnFavourite = holder.itemView.findViewById(R.id.btnFavourites);
            if(mFavourites.contains(mBookList.get(position)))
                mBtnFavourite.setBackground(mContext.getResources().getDrawable(R.drawable.ic_favorite));
            else
                mBtnFavourite.setBackground(mContext.getResources().getDrawable(R.drawable.ic_favorite_border));

            mBtnFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FirebaseDatabaseHelper()
                            .addFavourite(FirebaseAuth.getInstance().getUid(), mKeys.get(position),
                                    mBookList.get(position), mFavourites, mContext);
                    if(mFavourites.contains(mBookList.get(position)))
                        mBtnFavourite.setBackground(mContext.getResources().getDrawable(R.drawable.ic_favorite));
                    else
                        mBtnFavourite.setBackground(mContext.getResources().getDrawable(R.drawable.ic_favorite_border));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mBookList.size();
        }

        @Override
        public Filter getFilter() {
            return exampleFilter;
        }

        private Filter exampleFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Book> filteredList = new ArrayList<>();

                if(constraint == null || constraint.length() == 0) {
                    filteredList.addAll(mBookListFull);
                }
                else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for(Book book : mBookListFull) {
                        if(book.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(book);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mBookList.clear();
                mBookList.addAll((List) results.values);

                mKeys.clear();
                for(Book book : mBookList) {
                    mKeys.add(mKeysFull.get(mBookListFull.indexOf(book)));
                }
                notifyDataSetChanged();
            }
        };
    }

    class FavouritesAdapter extends RecyclerView.Adapter<BookItemView> implements Filterable {
        private List<Book> mBookList;
        private List<String> mKeys;
        private List<Book> mFavourites;
        private List<String> mFavouritesKeys;

        private List<Book> mFavouritesFull;
        List<String> mKeysFull;

        public FavouritesAdapter(List<Book> mBookList, List<String> mKeys, List<Book> mFavourites, List<String> mFavouritesKeys) {
            this.mBookList = mBookList;
            this.mKeys = mKeys;
            this.mFavourites = mFavourites;
            this.mFavouritesKeys = mFavouritesKeys;
            mFavouritesFull = new ArrayList<>(mFavourites);
            mKeysFull = new ArrayList<>(mKeys);
        }

        @NonNull
        @Override
        public BookItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookItemView holder, final int position) {
            holder.bind(mFavourites.get(position), mFavouritesKeys.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BookDetails.class);
                    intent.putExtra("key", mKeys.get(position));
                    intent.putExtra("title", mBookList.get(position).getTitle());
                    intent.putExtra("author", mBookList.get(position).getAuthor());
                    intent.putExtra("isbn", mBookList.get(position).getIsbn());
                    intent.putExtra("numberPages", mBookList.get(position).getNumberPages());
                    intent.putExtra("rating", mBookList.get(position).getRating());
                    intent.putExtra("publicationDate", mBookList.get(position).getPublicationDate());
                    intent.putExtra("description", mBookList.get(position).getDescription());
                    intent.putExtra("genres", mBookList.get(position).getGenres());
                    intent.putExtra("thumbnail", mBookList.get(position).getThumbnail());
                    intent.putExtra("isFave", mFavourites.contains(mBookList.get(position)));
                    mContext.startActivity(intent);
                }
            });

            final Button mBtnFavourite = holder.itemView.findViewById(R.id.btnFavourites);
            //if(mFavourites.contains(mBookList.get(position)))
                mBtnFavourite.setBackground(mContext.getResources().getDrawable(R.drawable.ic_favorite));
            //else
            //    mBtnFavourite.setBackground(mContext.getResources().getDrawable(R.drawable.ic_favorite_border));

            mBtnFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FirebaseDatabaseHelper()
                            .addFavourite(FirebaseAuth.getInstance().getUid(), mFavouritesKeys.get(position),
                                    mFavourites.get(position), mFavourites, mContext);
                    if(mFavourites.contains(mFavourites.get(position)))
                        mBtnFavourite.setBackground(mContext.getResources().getDrawable(R.drawable.ic_favorite));
                    else
                        mBtnFavourite.setBackground(mContext.getResources().getDrawable(R.drawable.ic_favorite_border));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFavourites.size();
        }

        @Override
        public Filter getFilter() {
            return exampleFilter;
        }

        private Filter exampleFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Book> filteredList = new ArrayList<>();

                if(constraint == null || constraint.length() == 0) {
                    filteredList.addAll(mFavouritesFull);
                }
                else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for(Book book : mFavouritesFull) {
                        if(book.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(book);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFavourites.clear();
                mFavourites.addAll((List) results.values);

                mFavouritesKeys.clear();
                for(Book book : mFavourites) {
                    mFavouritesKeys.add(mKeysFull.get(mFavouritesFull.indexOf(book)));
                }
                notifyDataSetChanged();
            }
        };
    }

    static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
