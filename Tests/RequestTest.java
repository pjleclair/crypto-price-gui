import Requests.Service;

import org.junit.Assert;
import org.junit.Test;

public class RequestTest {
    @Test
    public void testRequest() {
        Service service = new Service();
        try {
            String res = service.fetchData("bitcoin", "365");
            Assert.assertNotNull(res);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
