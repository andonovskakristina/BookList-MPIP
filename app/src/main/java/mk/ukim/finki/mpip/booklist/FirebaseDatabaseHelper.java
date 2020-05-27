package mk.ukim.finki.mpip.booklist;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telecom.Call;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

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

    public void addFavourite(String userId, final String bookId, Book book, List<Book> mFavourites, Context mContext) {
        if (mFavourites.contains(book)) {
            mReferenceFavourites.child(userId).child("books").child(bookId).setValue(null);
        }
        else {
            mReferenceFavourites.child(userId).child("books").child(bookId).setValue(book);

            // check if there are 5 fave books and push notification
            String message = "";
            if(mFavourites.size() + 1 == 5) {
                message = "You have a great taste in books! Five Books added to your Favourites List. " +
                        "Keep going and don't forget: \n\n “Books are a uniquely portable magic.”\n" +
                        "― Stephen King, On Writing: A Memoir of the Craft";
            }
            else if(mFavourites.size() + 1 == 10) {
                message = "Great job! Ten books added to your Favourites List. Keep being motivated. \n\n" +
                        "“I have always imagined that Paradise will be a kind of library.”\n" +
                        "― Jorge Luis Borges";
            }

            if(!message.isEmpty()) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_message)
                        .setContentTitle("New Notification")
                        .setContentText(message)
                        .setAutoCancel(true);

                Intent intent = new Intent(mContext, Notification.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("message", message);

                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
            }
        }
    }
}
