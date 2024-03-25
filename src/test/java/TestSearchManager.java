import com.markrestrepo.nih.SearchManager;
import com.markrestrepo.nih.model.SearchResponse;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestSearchManager {
    @Test
    void manage() throws URISyntaxException, IOException, ParserConfigurationException, SAXException, ExecutionException, InterruptedException {
        SearchManager manager = new SearchManager();
        SearchResponse sr = manager.search("diabetes");

        manager.threads.get(sr.getId()).get();
        System.out.println(manager.fetch(sr.getId()));
    }
}
