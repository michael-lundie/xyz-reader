package com.example.xyzreader.ui.utils;

import java.util.ArrayList;
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

    int paragraphCount;
    ArrayList<Integer> paragraphStartIndex;


    public TextRecyclerFeeder(String inputString) {
        this.inputString = testString;
        initFeeder();
    }

    /**
     * Simple method to count
     * @return
     */
    private void countParagraphs() {
        paragraphCount = 0;
        paragraphStartIndex = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\\\r\\\\n\\\\r\\\\n");
        Matcher matcher = pattern.matcher(inputString);
        while (matcher.find()) {
            paragraphCount++;
            paragraphStartIndex.add(paragraphCount, matcher.end());
        }
    }

    private void initFeeder() {
        if (textChunkArray == null) {
            textChunkArray = new ArrayList<>();
        }
        //Plan here is to replace the startIndex with the next split position.
        // each time recycler view calls for the next sections of data, it can start off
        // where it left.
        int startIndex = 0;

        //Gets the index where we want to end splitting the string
        // so we are not working our way through the whole set of data at once. The user
        // may not want to scroll that far down!
        // It would be wise to consider splitting this data into pages also. it is a lot to scroll through

        int splitPosition = ordinalIndexOf(inputString.substring(startIndex), "\\r\\n\\r\\n",7 );

        System.out.println("Split Position is: " + splitPosition);

        // so next we will create a substring of our 'working string'. Essentially we cut it into
        // a smaller chunk
        String stringToEdit = inputString.substring(0, splitPosition);

        // We chunk everything into an array using split here
        String[] splitString = stringToEdit.split("\\\\r\\\\n\\\\r\\\\n");

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

    public String fetchParagraphAtPosition(int Index) {

    }

    private String splitString(String inputString) {
        String[] splitString = inputString.split("\r\n\r\n", 8);
        return "null";
    }

    public String getFormattedTextChunk(int index) {
        return "null";
    }

    public String getTextForPosition(int index) {
        return textChunkArray.get(index);
    }

    String testString = "For starters, let me try to summarize the lessons and intuitions\\r\\n" +
            "I've had about ebooks from my release of two novels and most of a\\r\\n" +
            "short story collection online under a Creative Commons license. A\\r\\n" +
            "parodist who published a list of alternate titles for the\\r\\n" +
            "presentations at this event called this talk, \\\"eBooks Suck Right\\r\\n" +
            "Now,\\\" [eBooks suck right now] and as funny as that is, I don't\\r\\n" +
            "think it's true.\\r\\n" +
            "\\r\\n" +
            "No, if I had to come up with another title for this talk, I'd\\r\\n" +
            "call it: \\\"Ebooks: You're Soaking in Them.\\\" [Ebooks: You're\\r\\n" +
            "Soaking in Them] That's because I think that the shape of ebooks\\r\\n" +
            "to come is almost visible in the way that people interact with\\r\\n" +
            "text today, and that the job of authors who want to become rich\\r\\n" +
            "and famous is to come to a better understanding of that shape.\\r\\n" +
            "\\r\\n" +
            "I haven't come to a perfect understanding. I don't know what the\\r\\n" +
            "future of the book looks like. But I have ideas, and I'll share\\r\\n" +
            "them with you:\\r\\n" +
            "\\r\\n" +
            "1. Ebooks aren't marketing. [Ebooks aren't marketing] OK, so\\r\\n" +
            "ebooks *are* marketing: that is to say that giving away ebooks\\r\\n" +
            "sells more books. Baen Books, who do a lot of series publishing,\\r\\n" +
            "have found that giving away electronic editions of the previous\\r\\n" +
            "installments in their series to coincide with the release of a\\r\\n" +
            "new volume sells the hell out of the new book -- and the\\r\\n" +
            "backlist. And the number of people who wrote to me to tell me\\r\\n" +
            "about how much they dug the ebook and so bought the paper-book\\r\\n" +
            "far exceeds the number of people who wrote to me and said, \\\"Ha,\\r\\n" +
            "ha, you hippie, I read your book for free and now I'm not gonna\\r\\n" +
            "buy it.\\\" But ebooks *shouldn't* be just about marketing: ebooks\\r\\n" +
            "are a goal unto themselves. In the final analysis, more people\\r\\n" +
            "will read more words off more screens and fewer words off fewer\\r\\n" +
            "pages and when those two lines cross, ebooks are gonna have to be\\r\\n" +
            "the way that writers earn their keep, not the way that they\\r\\n" +
            "promote the dead-tree editions.\\r\\n" +
            "\\r\\n" +
            "2. Ebooks complement paper books. [Ebooks complement paper\\r\\n" +
            "books]. Having an ebook is good. Having a paper book is good.\\r\\n" +
            "Having both is even better. One reader wrote to me and said that\\r\\n" +
            "he read half my first novel from the bound book, and printed the\\r\\n" +
            "other half on scrap-paper to read at the beach. Students write to\\r\\n" +
            "me to say that it's easier to do their term papers if they can\\r\\n" +
            "copy and paste their quotations into their word-processors. Baen\\r\\n" +
            "readers use the electronic editions of their favorite series to\\r\\n" +
            "build concordances of characters, places and events. \\r\\n\\r\\n" +
            "1. Ebooks aren't marketing. [Ebooks aren't marketing] OK, so\\r\\n" +
            "ebooks *are* marketing: that is to say that giving away ebooks\\r\\n" +
            "sells more books. Baen Books, who do a lot of series publishing,\\r\\n" +
            "have found that giving away electronic editions of the previous\\r\\n" +
            "installments in their series to coincide with the release of a\\r\\n" +
            "new volume sells the hell out of the new book -- and the\\r\\n" +
            "backlist. And the number of people who wrote to me to tell me\\r\\n" +
            "about how much they dug the ebook and so bought the paper-book\\r\\n" +
            "far exceeds the number of people who wrote to me and said, \\\"Ha,\\r\\n" +
            "ha, you hippie, I read your book for free and now I'm not gonna\\r\\n" +
            "buy it.\\\" But ebooks *shouldn't* be just about marketing: ebooks\\r\\n" +
            "are a goal unto themselves. In the final analysis, more people\\r\\n" +
            "will read more words off more screens and fewer words off fewer\\r\\n" +
            "pages and when those two lines cross, ebooks are gonna have to be\\r\\n" +
            "the way that writers earn their keep, not the way that they\\r\\n" +
            "promote the dead-tree editions.\\r\\n" +
            "\\r\\n" +
            "2. Ebooks complement paper books. [Ebooks complement paper\\r\\n" +
            "books]. Having an ebook is good. Having a paper book is good.\\r\\n" +
            "Having both is even better. One reader wrote to me and said that\\r\\n" +
            "he read half my first novel from the bound book, and printed the\\r\\n" +
            "other half on scrap-paper to read at the beach. Students write to\\r\\n" +
            "me to say that it's easier to do their term papers if they can\\r\\n" +
            "copy and paste their quotations into their word-processors. Baen\\r\\n" +
            "readers use the electronic editions of their favorite series to\\r\\n" +
            "build concordances of characters, places and events. \\r\\n\\r\\n" +
            "1. Ebooks aren't marketing. [Ebooks aren't marketing] OK, so\\r\\n" +
            "ebooks *are* marketing: that is to say that giving away ebooks\\r\\n" +
            "sells more books. Baen Books, who do a lot of series publishing,\\r\\n" +
            "have found that giving away electronic editions of the previous\\r\\n" +
            "installments in their series to coincide with the release of a\\r\\n" +
            "new volume sells the hell out of the new book -- and the\\r\\n" +
            "backlist. And the number of people who wrote to me to tell me\\r\\n" +
            "about how much they dug the ebook and so bought the paper-book\\r\\n" +
            "far exceeds the number of people who wrote to me and said, \\\"Ha,\\r\\n" +
            "ha, you hippie, I read your book for free and now I'm not gonna\\r\\n" +
            "buy it.\\\" But ebooks *shouldn't* be just about marketing: ebooks\\r\\n" +
            "are a goal unto themselves. In the final analysis, more people\\r\\n" +
            "will read more words off more screens and fewer words off fewer\\r\\n" +
            "pages and when those two lines cross, ebooks are gonna have to be\\r\\n" +
            "the way that writers earn their keep, not the way that they\\r\\n" +
            "promote the dead-tree editions.\\r\\n" +
            "\\r\\n" +
            "2. Ebooks complement paper books. [Ebooks complement paper\\r\\n" +
            "books]. Having an ebook is good. Having a paper book is good.\\r\\n" +
            "Having both is even better. One reader wrote to me and said that\\r\\n" +
            "he read half my first novel from the bound book, and printed the\\r\\n" +
            "other half on scrap-paper to read at the beach. Students write to\\r\\n" +
            "me to say that it's easier to do their term papers if they can\\r\\n" +
            "copy and paste their quotations into their word-processors. Baen\\r\\n" +
            "readers use the electronic editions of their favorite series to\\r\\n" +
            "build concordances of characters, places and events. \\r\\n\\r\\n";

    // Static method from : https://programming.guide/java/nth-occurrence-in-string.html
    public int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }
}
