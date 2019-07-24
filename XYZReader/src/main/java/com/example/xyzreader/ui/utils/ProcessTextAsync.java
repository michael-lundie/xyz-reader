package com.example.xyzreader.ui.utils;

import android.os.AsyncTask;

public class ProcessTextAsync extends AsyncTask<String, Void, String> {

    private Listener listener;
    ProcessTextAsync(final Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onTextProcessed(final String string);
        void onTextProcessError();
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... strings) {
        final String inputString = strings[0];
        String string = null;

    }
}
