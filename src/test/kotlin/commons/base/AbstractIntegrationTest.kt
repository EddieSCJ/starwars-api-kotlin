package commons.base

import com.starwars.kotlin.StarWarsApiKotlinApplication
import org.springframework.beans.factory.annotation.Autowired
import com.starwars.kotlin.infra.external.config.database.MongoConfiguration
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
@ExtendWith(SpringExtension::class)
@ActiveProfiles(profiles = ["test", "base"])
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [StarWarsApiKotlinApplication::class, MongoConfiguration::class]
)
abstract class AbstractIntegrationTest @Autowired constructor()