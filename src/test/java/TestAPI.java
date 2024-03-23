import com.markrestrepo.nih.api;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TestAPI {

    @Test
    void search() throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
        URIBuilder uriBuilder = new URIBuilder("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi");
        URI uri1 = uriBuilder.addParameter("test", "foo").build();
        URI uri2 = uriBuilder.addParameter("test", "bar").build();

    }
}