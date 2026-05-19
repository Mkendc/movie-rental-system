package movieRental;

public class Movie {
    private String title;
    private int releaseYear;
    private String genre;
    private String rating;  // Changed from double to String for G, PG, PG-13, R, NC-17
    private double rentalFee;

    public Movie(String title, int releaseYear, String genre, String rating, double rentalFee) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.rating = rating;
        this.rentalFee = rentalFee;
    }

    public String getTitle() { return title; }
    public int getReleaseYear() { return releaseYear; }
    public String getGenre() { return genre; }
    public String getRating() { return rating; }  // Changed return type to String
    public double getRentalFee() { return rentalFee; }

    public void setTitle(String title) { this.title = title; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setRating(String rating) { this.rating = rating; }  // Changed parameter type to String
    public void setRentalFee(double rentalFee) { this.rentalFee = rentalFee; }
}
