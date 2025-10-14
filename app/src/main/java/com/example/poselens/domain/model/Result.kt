package com.example.poselens.domain.model

/**
 * A generic wrapper class for handling results with success/failure states
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    /**
     * Returns true if this is a Success result
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Returns true if this is an Error result
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Returns true if this is a Loading result
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Returns data if Success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Returns data if Success, or default value
     */
    fun getOrDefault(default: T): T = when (this) {
        is Success -> data
        else -> default
    }
    
    /**
     * Returns data if Success, or throws exception if Error
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is still loading")
    }
    
    /**
     * Transform Success data with the given transform function
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(exception, message)
        is Loading -> Loading
    }
    
    /**
     * Execute action on Success
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    /**
     * Execute action on Error
     */
    inline fun onError(action: (Exception) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
    
    /**
     * Execute action on Loading
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
    
    companion object {
        /**
         * Create a Success result
         */
        fun <T> success(data: T): Result<T> = Success(data)
        
        /**
         * Create an Error result
         */
        fun error(exception: Exception, message: String? = null): Result<Nothing> =
            Error(exception, message)
        
        /**
         * Create an Error result from a message
         */
        fun error(message: String): Result<Nothing> =
            Error(Exception(message), message)
        
        /**
         * Create a Loading result
         */
        fun loading(): Result<Nothing> = Loading
    }
}

/**
 * Extension function to wrap suspend function in Result
 */
suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.error(e, e.message)
    }
}

/**
 * Extension function to wrap function in Result
 */
fun <T> runCatchingResult(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.error(e, e.message)
    }
}
