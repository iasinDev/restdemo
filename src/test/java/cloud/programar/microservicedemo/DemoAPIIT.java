package cloud.programar.microservicedemo;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, 
     statements = "delete from products; "
                 +"insert into products (reference, name, price) values ('testAAAA', 'Dalek tshirt', 10.0); "
                 +"insert into products (reference, name, price) values ('testBBBB', 'Bad Wolf tshirt', 10.0); "
)
public class DemoAPIIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testSaveNewProduct() {
        Product product  = new Product(0, "TestCCCC", "The Doctor tshirt", new BigDecimal("10.00"));
        HttpEntity<Product> entity = new HttpEntity<>(product);
        ResponseEntity<Product> response
                = restTemplate.exchange("/products",  HttpMethod.POST, entity, Product.class);

        assertEquals("Invocation was a success", HttpStatus.OK,
                response.getStatusCode());

        assertEquals("The response is equivalent to the original",
                product, response.getBody());
        assertNotEquals("The response id is not 0 because it is already persisted", 0, response.getBody().getId());
    }

    @Test
    public void testFindProducts() {
        ResponseEntity<List> response
                = restTemplate.exchange("/products",  HttpMethod.GET, null, List.class);
        List<Product> list = response.getBody();

        assertEquals("Invocation was a success", HttpStatus.OK,
                response.getStatusCode());

        assertEquals("We have two products in the system",
                2, list.size());

    }

}
