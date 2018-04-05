package example;

import de.auktionmarkt.formular.integration.TestBeans;
import de.auktionmarkt.formular.integration.TestChooseableEntityRepository;
import de.auktionmarkt.formular.integration.TestController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@AutoConfigureMockMvc


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
@EnableAutoConfiguration
@SpringBootTest(classes = {TestEntityRepository.class, TestBeans.class, TestController.class})
public class ExampleTest {

    @Autowired
    private TestEntityRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void test() {
        repository.findAll();
    }
}
