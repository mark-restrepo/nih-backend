package com.markrestrepo.nih.model;

public class SearchResponse {
    private final String id;
    private final String query;
    private final String records;

    public SearchResponse(String id, String query, String records){
        this.id = id;
        this.query = query;
        this.records = records;
    }

    public String getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }

    public String getRecords() {
        return records;
    }
}
