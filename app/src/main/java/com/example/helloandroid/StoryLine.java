package com.example.helloandroid;

public class StoryLine {
    static String speaker(String line) {
        int separator = line.indexOf('｜');
        return separator < 0 ? "" : line.substring(0, separator).trim();
    }

    static String body(String line) {
        int separator = line.indexOf('｜');
        return separator < 0 ? line : line.substring(separator + 1);
    }
}
