package com.markrestrepo.nih;

import io.javalin.Javalin;

import java.net.URISyntaxException;


public class api {


    public static void main(String[] args) {
        SearchManager manager;
        try {
            manager = new SearchManager();
        } catch (URISyntaxException e) {
            System.out.println("API initialization failed");
            throw new RuntimeException(e);
        }
        Javalin app = Javalin.create(/*config*/)
                .get("/search/{term}", ctx ->
                        {
                            try {
                                ctx.json(manager.search(ctx.pathParam("term")));
                            } catch (Exception e) {
                                ctx.result(String.format("Error with request: %s", e.getMessage())).status(500);
                            }
                        }
                )
                .get("/fetch/{task_id}", ctx ->
                        {
                            try {
                                ctx.json(manager.fetch(ctx.pathParam("task_id")));
                            } catch (Exception e) {
                                ctx.result(String.format("Error with request: %s", e.getMessage())).status(500);
                            }
                        }
                )
                .start(7070);
    }
}