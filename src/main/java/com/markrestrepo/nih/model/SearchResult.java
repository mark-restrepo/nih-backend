package com.markrestrepo.nih.model;

import java.util.ArrayList;

public class SearchResult {
    public ArrayList<Integer> results;
    public Long endTime;

    public SearchResult(ArrayList<Integer> results, long endTime) {
        this.results = results;
        this.endTime = endTime;
    }
}
