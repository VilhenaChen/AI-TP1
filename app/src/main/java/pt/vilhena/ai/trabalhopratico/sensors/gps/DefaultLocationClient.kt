package pt.vilhena.ai.trabalhopratico.sensors.gps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient,
) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            // TODO: Check if the location permssion is active

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Check if GPS or Network are active
            val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnable && !isNetworkEnable) {
                throw LocationClient.LocationException("GPS IS NOT ACTIVE")
            }

            val request = LocationRequest.create().setInterval(interval)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    launch { send(result.lastLocation) }
                }
            }

            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}
