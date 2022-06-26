package com.starwars.kotlin.planets.app.storage.mongo.model

import com.starwars.kotlin.planets.domain.model.Planet
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Duration
import java.time.LocalDateTime

@Document(collection = "planets")
data class MongoPlanet(
    @Id
    @Field("_id")
    @Indexed(unique = true)
    val id: ObjectId? = null,

    @Indexed(unique = true)
    val name: String,
    val climate: List<String>,
    val terrain: List<String>,
    val movieAppearances: Int,

    @Field("created")
    var creationDate: LocalDateTime? = null
) {

    fun toDomain(): Planet {
        val now = LocalDateTime.now()
        if (creationDate === null) creationDate = now

        val daysBetween = Duration.between(creationDate, now).toDays()
        val id: String? = id?.toString()
        return Planet(
            id,
            name,
            climate,
            terrain,
            movieAppearances,
            daysBetween
        )
    }

    companion object {
        fun fromDomain(planet: Planet): MongoPlanet {
            val id: ObjectId? = if (planet.id != null) ObjectId(planet.id) else null
            return MongoPlanet(
                id,
                planet.name,
                planet.climate,
                planet.terrain,
                planet.movieAppearances,
                LocalDateTime.now()
            )
        }
    }
}