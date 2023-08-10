package pt.vilhena.ai.trabalhopratico.sensors.gps

import android.location.Location
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

interface LocationClient {
    fun getLocationUpdates(): Flow<Location>

    class LocationException(message: String) : Exception()
}
