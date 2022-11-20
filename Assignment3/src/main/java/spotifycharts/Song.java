package spotifycharts;

import java.util.Comparator;
import java.util.HashMap;

public class Song {

    public enum Language {
        NL, // Dutch
        EN, // English
        DE, // German
        FR, // French
        SP, // Spanish
        IT, // Italian
    }

    public enum Country {
        NL, // Netherlands
        UK, // United Kingdom
        DE, // Germany
        BE, // Belgium
        FR, // France
        SP, // Spain
        IT  // Italy
    }


    private final String artist;
    private final String title;
    private final Language language;
    private final HashMap<Country, Integer> streamsPerCountry;


    /**
     * Constructs a new instance of Song based on given attribute values
     */
    public Song(String artist, String title, Language language) {
        this.artist = artist;
        this.title = title;
        this.language = language;
        this.streamsPerCountry = new HashMap<>();
    }

    /**
     * Sets the given streams count for the given country on this song
     *
     * @param country
     * @param streamsCount
     */
    public void setStreamsCountOfCountry(Country country, int streamsCount) {
        streamsPerCountry.put(country, streamsCount);
    }

    /**
     * retrieves the streams count of a given country from this song
     *
     * @param country
     * @return
     */
    public int getStreamsCountOfCountry(Country country) {
        return streamsPerCountry.getOrDefault(country, 0);
    }

    /**
     * Calculates/retrieves the total of all streams counts across all countries from this song
     *
     * @return
     */
    public int getStreamsCountTotal() {
        return streamsPerCountry.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }


    /**
     * compares this song with the other song
     * ordening songs with the highest total number of streams upfront
     *
     * @param other the other song to compare against
     * @return negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestStreamsCountTotal(Song other) {
        return Integer.compare(other.getStreamsCountTotal(), this.getStreamsCountTotal());
    }

    /**
     * compares this song with the other song
     * ordening all Dutch songs upfront and then by decreasing total number of streams
     *
     * @param other the other song to compare against
     * @return negative number, zero or positive number according to Comparator conventions
     */
    public int compareForDutchNationalChart(Song other) {
        // TODO compare this song with the other song
        //  ordening all Dutch songs upfront and then by decreasing total number of streams

        return Comparator.comparing((Song song) -> song.getLanguage() == Language.NL ? 1 : -1)
                .thenComparing(Song::getStreamsCountTotal)
                .compare(other, this);
    }


    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return String.format("%s/%s{%s}(%d)", artist, title, language, getStreamsCountTotal());
    }

//    @Override
//    public String toString() {
//        return new StringJoiner("")
//                .add(artist + "/")
//                .add(title)
//                .add("{" + language + "}")
//                .add("(" + getStreamsCountTotal() + ")")
//                .toString();
//    }
}
