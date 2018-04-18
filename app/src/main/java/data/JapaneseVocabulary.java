package data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Limegrass on 4/4/2018.
 */

public class JapaneseVocabulary implements Parcelable {

    // Regexes, not sure if they should be const static.
    // Most vocab are enclosed in the braces.
    public static final String EXACT_WORD_REGEX = "(?<=［).*(?=］)";

    // Try to find a word beginning with or enclosed with Kanji
    public static final String WORD_WITH_KANJI_REGEX =
            "\\p{Han}+[\\p{Hiragana}|\\p{Katakana}]*\\p{Han}*";

    // For finding only the kana of a word.
    public static final String KANA_REGEX = "[\\p{Hiragana}|\\p{Katakana}]+";

    public static final String READING_REGEX =
            "[\\p{Hiragana}|\\p{Katakana}]+(?=($|[\\p{Han}０-９]|\\d|\\s))";
    public static final String TONE_REGEX = "[\\d０-９]+";

    // Some messy dictionary entries have triangles in 
    public static final String TRIANGLES_REGEX = "[△▲]";

    private String word;
    private String reading;
    private String defintion;
    private String pitch;

    /**
     * Constructor given a string containing the word and a string containing the definition.
     * @param wordSource a string that contains the source of the word.
     * @param definitionSource a string containing the definition of the word.
     */
    public JapaneseVocabulary(String wordSource, String definitionSource){
        defintion = definitionSource;
        word = isolateWord(wordSource);
        reading = isolateReading(wordSource);
        pitch = isolatePitch(wordSource);
    }

    /**
     * Checks if the two JapaneseVocabulary objects have the same word, reading, and definition.
     * @param obj another JapaneseVocabulary instance
     * @return whether the two instances has the same word, reading, and definition
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }

        if(!(obj instanceof JapaneseVocabulary)){
            return false;
        }
        JapaneseVocabulary other = (JapaneseVocabulary) obj;

        //furigana is generated, pitch should not change
        //Maybe not include definition in case of different site definitions or formatting.
        return word.equals(other.word)
                && reading.equals(other.reading)
                && defintion.equals(other.defintion);
    }

    /**
     * Hash code function
     * @return a hashcode for the vocabulary word
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + word.hashCode();
        hash = 31 * hash + reading.hashCode();
        hash = 31 * hash + defintion.hashCode();
        hash = 31 * hash + pitch.hashCode();
        return hash;
    }

    // TODO: Maybe do something in Sanseido search for related words so this can be private

    /**
     * Isolates the full word from the possibly messy Sanseido html source
     * @param wordSource the raw string from the html source
     * @return The full word isolated from any furigana readings or tones
     */
    public static String isolateWord(String wordSource){
        wordSource = wordSource.replaceAll(TRIANGLES_REGEX, "");
        Matcher exactMatcher = Pattern.compile(EXACT_WORD_REGEX).matcher(wordSource);
        Matcher kanjiMatcher = Pattern.compile(WORD_WITH_KANJI_REGEX).matcher(wordSource);
        Matcher kanaMatcher = Pattern.compile(KANA_REGEX).matcher(wordSource);

        if(exactMatcher.find()){
            return exactMatcher.group(0).toString();
        }
        else if (kanjiMatcher.find()){
            return kanjiMatcher.group(0).toString();
        }
        else if (kanaMatcher.find()){
            return kanaMatcher.group(0).toString();
        }
        else {
            return wordSource;
        }
    }

    /**
     * Getter for the Japanese word (in Kanji or it's most common dictionary script).
     * @return the Japanese word in the form it appeared in the dictionary.
     */
    public String getWord() {
        return word;
    }

    /**
     * Generates an Anki format furigana string from the word and reading saved.
     * @return a string for Anki's furigana display.
     */
    public String getFurigana() {
        if (word.equals(reading)){
            return reading;
        }

        return word + "[" + reading + "]";
    }

    /**
     * Getter for the reading in kana of the word.
     * @return a string of the kana reading of the vocabulary word.
     */
    public String getReading() {
        return reading;
    }

    /**
     * Getter for the definition of the word.
     * @return a string of the definition of the word.
     */
    public String getDefintion() {
        return defintion;
    }

    /**
     * Getter for the pitch of the word.
     * @return a string representing the pitch of the word.
     */
    public String getPitch(){
        return pitch;
    }

    /**
     * Finds the tone from the given word source information
     * @param wordSource the raw information about the word
     * @return a string of the pitch of the word
     */
    private String isolatePitch(String wordSource){
        if(wordSource == null || wordSource.equals("")){
            return "";
        }

        String tone = "";
        Matcher toneMatcher = Pattern.compile(TONE_REGEX).matcher(wordSource);
        if (toneMatcher.find()){
            tone = toneMatcher.group(0).toString();
        }
        return tone;
    }

    /**
     * Helper method to isolate the reading of a Japanese vocabulary word from its source string.
     * @param wordSource the raw string containing the vocabulary word.
     * @return a string with the isolated kana reading of the word.
     */
    private String isolateReading(String wordSource){
        if(wordSource == null || wordSource.equals("")){
            return "";
        }

        Matcher readingMatcher = Pattern.compile(READING_REGEX).matcher(wordSource);
        if(readingMatcher.find()){
            return readingMatcher.group(0).toString();
        }
        return wordSource;
    }


    /**
     * Part of necessary methods to override to make the object parcelable.
     * @return the hash code of the JapaneseVocabulary.
     */
    @Override
    public int describeContents() {
        return hashCode();
    }

    /**
     * parcelization of the JapaneseObject, called when passed between activities.
     * @param parcel
     * @param i
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(word);
        parcel.writeString(reading);
        parcel.writeString(defintion);
        parcel.writeString(pitch);
    }

    public static final Parcelable.Creator<JapaneseVocabulary> CREATOR
            = new Parcelable.Creator<JapaneseVocabulary>(){
        @Override
        public JapaneseVocabulary createFromParcel(Parcel parcel) {
            return new JapaneseVocabulary(parcel);
        }

        @Override
        public JapaneseVocabulary[] newArray(int size) {
            return new JapaneseVocabulary[size];
        }
    };

    /**
     * constructor to unpack the parcel information for passing between activities.
     * @param parcel
     */
    private JapaneseVocabulary(Parcel parcel){
        word = parcel.readString();
        reading = parcel.readString();
        defintion = parcel.readString();
        pitch = parcel.readString();
    }
}
