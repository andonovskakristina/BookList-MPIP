package mk.ukim.finki.mpip.booklist;

import android.telecom.Call;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceBooks;
    private List<Book> books = new ArrayList<>();
    private List<String> keys = new ArrayList<>();

    private DatabaseReference mReferenceFavourites;
    private List<Book> favourites = new ArrayList<>();

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceBooks = mDatabase.getReference("books");
        mReferenceFavourites = mDatabase.getReference("user_favourites");
    }

    public interface DataStatus {
        void DataIsLoaded(List<Book> books, List<String> keys);
    }

    public interface FavouritesStatus {
        void FavouritesLoaded(List<Book> books, List<String> favouritesKeys);
    }

    public void readBooks(final DataStatus dataStatus) {
        mReferenceBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                books.clear();
                keys.clear();

                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Book book = keyNode.getValue(Book.class);
                    books.add(book);
                }
                dataStatus.DataIsLoaded(books, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public List<Book> readFavourites(String userId, final FavouritesStatus dataStatus) {
        final List<Book> mFavourites = new ArrayList<>();
        final List<String> mFavouritesKeys = new ArrayList<>();

        mReferenceFavourites.child(userId).child("books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFavourites.clear();
                mFavouritesKeys.clear();

                for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    mFavouritesKeys.add(keyNode.getKey());
                    Book book = keyNode.getValue(Book.class);
                    mFavourites.add(book);
                }
                dataStatus.FavouritesLoaded(mFavourites, mFavouritesKeys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return mFavourites;
    }

    public void addFavourite(String userId, final String bookId, Book book, List<Book> mFavourites) {
        if (mFavourites.contains(book)) {
            mReferenceFavourites.child(userId).child("books").child(bookId).setValue(null);
        }
        else {
            mReferenceFavourites.child(userId).child("books").child(bookId).setValue(book);
        }
    }
}
