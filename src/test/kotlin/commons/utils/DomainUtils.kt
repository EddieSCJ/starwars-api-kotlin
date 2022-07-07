package commons.utils

import com.appmattus.kotlinfixture.Fixture
import com.appmattus.kotlinfixture.kotlinFixture
import com.starwars.kotlin.planets.app.storage.mongo.model.MongoPlanet
import com.starwars.kotlin.planets.domain.model.Planet
import com.starwars.kotlin.planets.domain.model.client.ClientPlanetJson
import com.starwars.kotlin.planets.domain.model.client.PlanetResponseJson
import com.starwars.kotlin.planets.domain.model.view.PlanetJson
import org.bson.types.ObjectId
import java.util.*

object DomainUtils {
    @JvmField
    val FAKE_ID: String = ObjectId.get().toHexString()
    private val FAKER: Fixture = kotlinFixture()
    private val RANDOM = Random()

    @JvmStatic
    val randomPlanet: Planet
        get() {
            val weathers = listOf("Calor da misera", "Frio do carai")
            val terrain = listOf("terreno cheio", "terreno baldio")
            return Planet(ObjectId.get().toString(), "fake_name", weathers, terrain, RANDOM.nextInt(), 0L)
        }

    @JvmStatic
    val randomWithoutIdPlanet: Planet
        get() {
            val weathers = listOf("Calor da misera", "Frio do carai")
            val terrain = listOf("terreno cheio", "terreno baldio")
            return Planet(id = null, name = "fake_name", weathers, terrain, movieAppearances = RANDOM.nextInt(), cacheInDays = 0L)
        }


    val randomPlanetList: List<Planet> get() {
            val planets: MutableList<Planet> = ArrayList<Planet>()
            for (i in 0..2) { planets.add(randomPlanet) }
            return planets
        }

    val invalidPlanet: Planet get() = Planet(null, "", emptyList(), emptyList(), 0, 0L)

    @JvmStatic
    val randomMongoPlanet: MongoPlanet get() = MongoPlanet.fromDomain(randomPlanet)

    val randomMongoPlanetList: List<MongoPlanet> get() {
            val mongoPlanets: MutableList<MongoPlanet> = ArrayList<MongoPlanet>()
            for (i in 0..2) { mongoPlanets.add(randomMongoPlanet) }
            return mongoPlanets
        }

    val randomPlanetResponseJson: PlanetResponseJson
        get() {
            val films: List<String> = listOf("Explodindo a estrela da morte", "O menino do sabre de luz")
            val weathers = "Jurubeba do sertão, Frio de lascar"
            val terrain = "Semiarido, arido"

            val clientPlanetJson = ClientPlanetJson(name = "Gangrena", weathers, terrain, films)

            val planets: List<ClientPlanetJson> = listOf(clientPlanetJson)

            return PlanetResponseJson(1, planets)
        }


    val emptyPlanetResponseJson: PlanetResponseJson get() { return PlanetResponseJson(0, emptyList()) }

    @JvmStatic
    val randomPlanetJson: PlanetJson
        get() {
            val weathers = listOf("tempo ruim", "tempo bom")
            val terrain = listOf("arido", "semiarido")
            return PlanetJson(
                id = "id-aleatorio",
                name = "nome-aleatorio",
                weathers,
                terrain,
                movieAppearances = 0,
                cacheInDays = 0
            )
        }

    val randomPlanetJsonWithoutId: PlanetJson
        get() {
            val weathers = listOf("tempo ruim", "tempo bom")
            val terrain = listOf("arido", "semiarido")
            return PlanetJson(
                name = "nome-aleatorio",
                climate = weathers,
                terrain = terrain,
                movieAppearances = 0,
                cacheInDays = 0
            )
        }


    val invalidPlanetJson: PlanetJson get() = PlanetJson(id = null, name = "", emptyList(), emptyList(), movieAppearances = 0, cacheInDays = 0)
}