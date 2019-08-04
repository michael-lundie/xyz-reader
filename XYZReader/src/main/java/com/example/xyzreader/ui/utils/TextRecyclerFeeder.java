package com.example.xyzreader.ui.utils;

import android.util.Log;
import android.util.SparseIntArray;

import com.example.xyzreader.ui.adapters.TextRecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class responsible for taking a large input string/text passage, splitting each
 * paragraph into a chunk and assigning that chunk to an ArrayList of paragraphs.
 * Class containers getter methods for use with a recycler view adapter.
 */
//NOTES: We don't want to load all the text straight away. Waste of resources. What if we gradually
    // add our text to an array list and update the viewer. Then notify range updated.
    // we need to observe the current item the user is looking at, then make sure we load the next
    // batch of text.
public class TextRecyclerFeeder {
    private static final String LOG_TAG = TextRecyclerFeeder.class.toString();

    String inputString;
    ArrayList<String> textChunkArray;
    private SparseIntArray paragraphMap;

    int currentFeedIndex;

    private int numberOfParagraphs;


    public TextRecyclerFeeder(String inputString) {
        this.inputString = inputString;
        testInitFeeder();
    }

    private void testInitFeeder() {

        paragraphMap = buildParagraphMap(inputString);

    }

    public int getParagraphCount() {
        return numberOfParagraphs;
    }

    public String getParagraph(int position) {
        if (paragraphMap == null) {
            return "error";
        }

        int startIndex = 0;

        if(position != 0) {
            startIndex = paragraphMap.get(position-1);
        }

        int endIndex = paragraphMap.get(position);

        String outputString = inputString.substring(startIndex, endIndex);
        return outputString;
    }

    private void initFeeder() {

        buildParagraphMap(inputString);

        if (textChunkArray == null) {
            textChunkArray = new ArrayList<>();
        }

        // Plan here is to replace the startIndex with the next split position.
        // each time recycler view calls for the next sections of data, it can start off
        // where it left.
        int startIndex = 0;

        //Gets the index where we want to end splitting the string
        // so we are not working our way through the whole set of data at once. The user
        // may not want to scroll that far down!
        // Currently we are splitting at the end of each paragraph.
        // It would be wise to consider splitting this data into pages also. it is a lot to scroll through

        int splitPosition = ordinalIndexOf(inputString.substring(startIndex), "\r\n\r\n",7 );

        // Is it better to build a hash table with the paragraph number and the start and end index
        // of each paragraph, which is then looked up before extraction?

        paragraphMap = new SparseIntArray();

        System.out.println("Split Position is: " + splitPosition);

        // so next we will create a substring of our 'working string'. Essentially we cut it into
        // a smaller chunk
        String stringToEdit = inputString.substring(0, splitPosition);

        // We chunk everything into an array using split here
        //String[] splitString = stringToEdit.split("\\\\r\\\\n\\\\r\\\\n");
        String[] splitString = stringToEdit.split("\r\n\r\n");

        // Now we process each individual paragraph to perform necessary string operations.
        for(int i = 0; i < splitString.length; i++ ) {
            System.out.println("Processing Input " + i);

            if (splitString[i] != null) {
                // Add the formatted paragraph to our ArrayList for caching
                //We should probably hook our recycler viewer up to an interface here.
                textChunkArray.add(splitString[i].replaceAll("\\\\r\\\\n", " ")
                        .replaceAll("\\\\\"", "\""));
                System.out.println(textChunkArray.get(i));
            } else {
                System.out.println("String " + i + " is null.");
            }
        }
    }

    private int getNumberOfParagraphs(String inputString) {
        int numberOfParagraphs = 0;
        Pattern pattern = Pattern.compile("\r\n\r\n");
        Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            numberOfParagraphs++; //TODO: Check this is working with large text.
        } return numberOfParagraphs;

    }

    private SparseIntArray buildParagraphMap(String inputString) {
        numberOfParagraphs = 0;
        SparseIntArray mParagraphMap = new SparseIntArray();

        Pattern pattern = Pattern.compile("\r\n\r\n");
        Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            mParagraphMap.append(numberOfParagraphs, matcher.end());
            System.out.println("Current Map Index --> " + numberOfParagraphs +
                    " End @" + matcher.end());
            numberOfParagraphs++; //
        }
        Log.e(LOG_TAG, "Total number of Paragraphs -->>> " + numberOfParagraphs);
        return mParagraphMap;
    }

    private String splitString(String inputString) {
        String[] splitString = inputString.split("\r\n\r\n", 8);
        return "null";

    }

    // Static method from : https://programming.guide/java/nth-occurrence-in-string.html
    private int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }
}
