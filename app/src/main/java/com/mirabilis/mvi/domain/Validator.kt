package com.mirabilis.mvi.domain

object Validator {
    object Email {
        fun isValid(email: String): Boolean =
            email.matches(
                ("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+").toRegex()
            )
    }

    object Password {
        fun isValid(password: String): Boolean = password.length in 8 ..24
        fun isConfirmed(password: String, passwordConfirmation: String): Boolean =
            password == passwordConfirmation
    }
}
