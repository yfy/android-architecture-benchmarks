import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

open class BaseRepository {
    fun <T> sendRequest(call: suspend() -> T): Flow<T> = flow {
        val response = call.invoke()
        delay(DELAY)
        emit(response)
    }.flowOn(Dispatchers.IO)

    companion object {
        const val DELAY: Long = 500L
    }
}