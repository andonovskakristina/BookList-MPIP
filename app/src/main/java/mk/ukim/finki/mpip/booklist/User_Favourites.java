package mk.ukim.finki.mpip.booklist;

import java.util.List;

public class User_Favourites {
    private String userId;
    private List<String> favourites;

    public User_Favourites() {
    }

    public User_Favourites(String userId, List<String> favourites) {
        this.userId = userId;
        this.favourites = favourites;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<String> favourites) {
        this.favourites = favourites;
    }
}
