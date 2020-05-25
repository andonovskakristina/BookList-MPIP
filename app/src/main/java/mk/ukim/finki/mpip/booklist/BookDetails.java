package mk.ukim.finki.mpip.booklist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class BookDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        if(getIntent().hasExtra("title") && getIntent().hasExtra("author") && getIntent().hasExtra("isbn") &&
                getIntent().hasExtra("numberPages") && getIntent().hasExtra("rating") && getIntent().hasExtra("genres") &&
                getIntent().hasExtra("publicationDate") && getIntent().hasExtra("description") &&
                getIntent().hasExtra("thumbnail")) {
            setValues(getIntent().getStringExtra("title"), getIntent().getStringExtra("author"),
                    getIntent().getStringExtra("isbn"), getIntent().getIntExtra("numberPages", 0),
                    getIntent().getStringExtra("publicationDate"), getIntent().getDoubleExtra("rating", 0),
                    getIntent().getStringExtra("genres"), getIntent().getStringExtra("description"),
                    getIntent().getStringExtra("thumbnail"));
        }
    }

    public void setValues(String title, String author, String isbn, int numberPages, String publicationDate, double rating, String genres,
                          String description, String thumbnailUrl) {
        ((TextView)findViewById(R.id.txtBookTitle)).setText(title);
        ((TextView)findViewById(R.id.txtBookAuthor)).setText(author);
        ((TextView)findViewById(R.id.txtBookIsbn)).setText("ISBN: " + isbn);
        ((TextView)findViewById(R.id.txtBookNumberPages)).setText("Number of Pages: " + numberPages);
        ((TextView)findViewById(R.id.txtBookPublicationDate)).setText("Publication Date: " + publicationDate);
        ((TextView)findViewById(R.id.txtBookRating)).setText(String.valueOf(rating));
        ((TextView)findViewById(R.id.txtBookGenres)).setText(genres);
        ((TextView)findViewById(R.id.txtBookDescription)).setText(description);
        ((RatingBar)findViewById(R.id.ratingBar)).setRating((float) rating);
        new RecyclerView_Config.DownloadImageTask((ImageView)findViewById(R.id.thumbnail))
                .execute(thumbnailUrl);
    }
}
