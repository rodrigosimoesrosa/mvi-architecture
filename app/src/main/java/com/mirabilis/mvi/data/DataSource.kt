package com.mirabilis.mvi.data

import kotlinx.coroutines.delay
import kotlin.random.Random

class DataSource {
    suspend fun signIn(email: String, password: String): Boolean {
        delay(2000)
        return if (Random.nextBoolean()) true
        else throw Throwable("Sign In Exception")
    }

    suspend fun signUp(email: String, password: String): Boolean {
        delay(2000)
        return if (Random.nextBoolean()) true
        else throw Throwable("Sign Up Exception")
    }
}
