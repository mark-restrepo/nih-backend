package com.markrestrepo.nih;

import com.markrestrepo.nih.model.SearchResponse;
import com.markrestrepo.nih.model.SearchResult;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Search Manager object that handles requests and processes results.
 */
public class SearchManager {
    public ThreadPoolExecutor executor;
    public HashMap<String, Future<SearchResult>> threads;
    public HashMap<String, Long> startTimes;
    private String baseURI = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi";

    private SimpleDateFormat dateFormatter;


    public SearchManager() throws URISyntaxException {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        this.threads = new HashMap<>();
        this.startTimes = new HashMap<>();
        this.dateFormatter = new SimpleDateFormat("YYYY-MM-d HH:mm:ss");
        this.dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /***
     * Submits an async search job to the thread pool, stores the result in the datastore
     *
     * @param term The search term to look for in pubmed
     * @return Placeholder search response
     */
    public SearchResponse search(String term) throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
        // Check how many records will be present
        URIBuilder builder = new URIBuilder(this.baseURI);
        URI checkUri = builder
                .addParameter("term", term)
                .addParameter("rettype", "count")
                .build();
        String uid = UUID.randomUUID().toString();
        Document doc = submitGet(checkUri);
        int records = getCount(doc);

        // Submit a thread to process the search and retrieve all results.
        this.threads.put(
                uid,
                this.executor.submit(() -> processSearch(uid, term, this.baseURI))
        );
        this.startTimes.put(uid, System.currentTimeMillis());

        // Return the placeholder response
        return new SearchResponse(uid, term, String.format("%d", records));
    }

    /***
     * Async static method that grabs the full, massive list of id's
     *
     * @param term Search term
     * @param baseURI Base url to hit
     * @return List of the record id's
     */
    public static SearchResult processSearch(String id, String term, String baseURI) throws URISyntaxException,
            IOException, ParserConfigurationException, SAXException, InterruptedException {
        int retMax = 10000;
        ArrayList<Integer> retList = new ArrayList<>();

        // The API limits pubmed searches to 10,000 results, so that's all we can return
        URIBuilder builder = new URIBuilder(baseURI);
        URI uri = builder
            .addParameter("term", term)
            .addParameter("retmax", String.format("%d", retMax))
            .build();

        // Parse the list of id's from the batch
        Document doc = submitGet(uri);
        NodeList idList = doc.getElementsByTagName("Id");
        for (int n = 0; n < idList.getLength(); n++){
            retList.add(Integer.parseInt(idList.item(n).getTextContent()));
        }

        return new SearchResult(retList, System.currentTimeMillis());
    }

    /***
     * Retrieves the results of a previously executed query, if available
     *
     * @param taskId Task id to search for
     * @return a response w/ the status of the task, and results if available
     */
    public HashMap fetch(String taskId) throws ExecutionException, InterruptedException {
        Future<SearchResult> res = threads.get(taskId);
        if (res == null) {
            throw new NoSuchElementException(String.format("No results found for id %s", taskId));
        }
        Long createTime = this.startTimes.get(taskId);
        SearchResult sr = res.get();
        HashMap<Object, Object> resultMap = new HashMap<>();
        resultMap.put("task_id", taskId);
        resultMap.put("created_time", this.dateFormatter.format(createTime));

        if (res.isDone()) {
            resultMap.put("result", new HashMap<>());
            ((HashMap) resultMap.get("result")).put("pmids", sr.results);
            resultMap.put("status", "completed");
            resultMap.put("run_seconds", (int) (sr.endTime - createTime)/1000);
        } else {
            resultMap.put("status", "processing");
        }
        return resultMap;
    }

    /***
     * Helper function to get the count of records from the doc.
     *
     * @param doc search result
     * @return count of records
     */
    private static int getCount(Document doc) {
        return Integer.parseInt(doc.getElementsByTagName("Count").item(0).getTextContent());
    }

    /***
     * Helper function that submits a get request, and parse the doc
     *
     * @param uri exact uri to submit as get request
     * @return parsed xml document
     */
    private static Document submitGet(URI uri) throws IOException, ParserConfigurationException, SAXException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(uri);

        CloseableHttpResponse response = client.execute(request);
        InputStream is = response.getEntity().getContent();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        return docBuilder.parse(is);
    }


}