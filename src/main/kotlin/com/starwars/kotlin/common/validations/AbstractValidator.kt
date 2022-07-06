package com.starwars.kotlin.common.validations

import com.starwars.kotlin.infra.helpers.MessageSourceHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.reflect.Field
import java.text.MessageFormat

@Component
abstract class AbstractValidator constructor(private val messageSourceHelper: MessageSourceHelper) {
    protected lateinit var validationMessages: MutableList<String>

    fun validate(generic: Any): MutableList<String> {
        val declaredFields = generic.javaClass.declaredFields
        validationMessages = mutableListOf()

        for (field in declaredFields) {
            field.isAccessible = true
            callGenericValidators(field, generic)
        }

        return validationMessages
    }

    @Throws(IllegalAccessException::class)
    protected fun callGenericValidators(field: Field, generic: Any) {
        val value = field[generic]
        isNull(field, value)
        isBlank(field, value)
        isEmptyList(field, value)
        isEmptyArray(field, value)
    }

    protected fun addFieldErrorMessage(field: String?, message: String?) {
        validationMessages.add(MessageFormat.format("Campo {0} {1}", field, message))
    }

    private fun isBlank(field: Field, value: Any?) {
        if (field.type === String::class.java && value !== null && value === "") {
            val errorMessage: String = messageSourceHelper.getFieldErrorMessage("blank")
            addFieldErrorMessage(field.name, errorMessage)
        }
    }

    protected fun isNull(field: Field, value: Any? = null) {
        if (value === null) {
            val errorMessage: String = messageSourceHelper.getFieldErrorMessage("null")
            addFieldErrorMessage(field.name, errorMessage)
        }
    }

    private fun isEmptyList(field: Field, value: Any? = null) {
        if(value==null) return
        if (field.type === MutableList::class.java) {
            val list = value as List<*>
            if (list.isEmpty()) {
                val errorMessage: String = messageSourceHelper.getFieldErrorMessage("empty")
                addFieldErrorMessage(field.name, errorMessage)
            }
        }
    }

    private fun isEmptyArray(field: Field, value: Any? = null) {
        if(value==null) return
        if (field.type.isArray) {
            val array = value as Array<*>
            if (array.isEmpty()) {
                val errorMessage: String = messageSourceHelper.getFieldErrorMessage("empty")
                addFieldErrorMessage(field.name, errorMessage)
            }
        }
    }
}