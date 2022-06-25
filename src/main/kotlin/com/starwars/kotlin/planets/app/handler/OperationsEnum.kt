package com.starwars.kotlin.planets.app.handler

import org.springframework.http.HttpMethod

enum class OperationsEnum(val operationName: String, val method: HttpMethod, val path: String) {
    GET_PLANETS_PAGE("get_planets_page", HttpMethod.GET, Constants.PLANETS_ENDPOINT),
    GET_PLANET_BY_NAME("get_planet_by_name", HttpMethod.GET, Constants.PLANETS_ENDPOINT + "/?search={name}"),
    GET_PLANET_BY_ID("get_planet_by_id", HttpMethod.GET, Constants.PLANETS_ENDPOINT + "/{id}"),
    CREATE_PLANET("create_planet", HttpMethod.POST, Constants.PLANETS_ENDPOINT),
    UPDATE_PLANET("update_planet", HttpMethod.PUT, Constants.PLANETS_ENDPOINT),
    DELETE_PLANET("delete_planet", HttpMethod.DELETE, Constants.PLANETS_ENDPOINT + "/{id}");
}