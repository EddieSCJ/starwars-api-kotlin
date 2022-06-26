package com.starwars.kotlin.planets.app.validations

import com.starwars.kotlin.common.validations.AbstractValidator
import com.starwars.kotlin.infra.helpers.MessageSourceHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.reflect.Field

@Component
class PlanetValidator constructor(private val messageSourceHelper: MessageSourceHelper) : AbstractValidator(messageSourceHelper) {
    override fun isNull(field: Field, value: Any?) {
        if (value === null && field.name !== "cacheInDays"
            && field.name != "id"
        ) {
            val errorMessage: String = messageSourceHelper.getFieldErrorMessage("null")
            addFieldErrorMessage(field.name, errorMessage)
        }
    }
}