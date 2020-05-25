package mk.ukim.finki.mpip.booklist;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private int numberPages;
    private String publicationDate;
    private String thumbnail;
    private String description;
    private double rating;
    private String genres;

    public Book() {
    }

    public Book(String title, String author, String isbn, int numberPages, String publicationDate, String thumbnail, String description, double rating, String genres) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.numberPages = numberPages;
        this.publicationDate = publicationDate;
        this.thumbnail = thumbnail;
        this.description = description;
        this.rating = rating;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getNumberPages() {
        return numberPages;
    }

    public void setNumberPages(int numberPages) {
        this.numberPages = numberPages;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual= false;

        if (object != null && object instanceof Book)
        {
            isEqual = (this.title.equals(((Book) object).title) && this.author.equals(((Book) object).author) &&
                    this.isbn.equals(((Book) object).isbn) && this.publicationDate.equals(((Book) object).publicationDate) &&
                    this.description.equals(((Book) object).description) && this.genres.equals(((Book) object).genres) &&
                    this.thumbnail.equals(((Book) object).thumbnail) && this.rating == ((Book) object).rating &&
                    this.numberPages == ((Book) object).numberPages);
        }

        return isEqual;
    }
}
