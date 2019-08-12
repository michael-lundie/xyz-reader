package com.example.xyzreader.utils;

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
    // batch of text. It might be better to somehow split content into pages.
public class TextRecyclerFeeder {
    private static final String LOG_TAG = TextRecyclerFeeder.class.toString();

    private String inputString;
    private SparseIntArray paragraphMap;
    private int numberOfParagraphs;

    /**
     * Constructor for TextRecyclerFeeder
     * @param inputString initial text input!
     */
    public TextRecyclerFeeder(String inputString) {
        this.inputString = inputString;
        initFeeder();
    }

    /**
     * Initialise the text feeder.
     */
    private void initFeeder() {
        paragraphMap = buildParagraphMap(inputString);
    }

    /**
     * Getter method to return the number of paragraphs in the text array.
     * @return number of paragraphs as integer
     */
    public int getParagraphCount() {
        return numberOfParagraphs;
    }

    /**
     * Getter method which returns the paragraph corresponding to an adapter position.
     * (designed to be used in conjunction with recycler adapter)
     * @param position position of adapter
     * @return formatted paragraph as String
     */
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

    /**
     * Simple method to do minor formatting on output text.
     * @param outputString string to be formatted
     * @return formatted output string
     */
    private String formatOutput(String outputString) {
        return outputString.replaceAll("\r\n", "");
    }

    /**
     * Method builds a map of paragraphs from the input text, in order to more efficiently
     * organise data to be formatted and displayed on screen.
     * @param inputString initial text input
     * @return SparseIntArray of paragraphs.
     */
    private SparseIntArray buildParagraphMap(String inputString) {
        numberOfParagraphs = 0;
        SparseIntArray mParagraphMap = new SparseIntArray();

        Pattern pattern = Pattern.compile("\r\n\r\n");
        Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            mParagraphMap.append(numberOfParagraphs, matcher.end());
            numberOfParagraphs++;
        }
        return mParagraphMap;
    }
}