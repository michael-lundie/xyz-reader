package com.example.xyzreader.utils;

import android.util.Log;
import android.util.SparseIntArray;

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
    private SparseIntArray paragraphMap;
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
        return formatOutput(outputString);
    }

    private String formatOutput(String outputString) {
        String formattedString = outputString.replaceAll("\r\n", "");
        return formattedString;
    }

    private SparseIntArray buildParagraphMap(String inputString) {
        numberOfParagraphs = 0;
        SparseIntArray mParagraphMap = new SparseIntArray();

        Pattern pattern = Pattern.compile("\r\n\r\n");
        Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            mParagraphMap.append(numberOfParagraphs, matcher.end());
            numberOfParagraphs++;
        }
        //TODO: Remove logs
        Log.e(LOG_TAG, "Total number of Paragraphs -->>> " + numberOfParagraphs);
        return mParagraphMap;
    }
}