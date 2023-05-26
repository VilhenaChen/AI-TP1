package pt.vilhena.ai.trabalhopratico.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import pt.vilhena.ai.trabalhopratico.data.SensorCaptureService
import java.time.LocalDateTime
import java.util.Calendar
import java.util.UUID

class ActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentActivity = MutableLiveData<String>()
    val currentActivity = _currentActivity

    private lateinit var fusedLocationManager: FusedLocationProviderClient
    private var sensorCaptureService = SensorCaptureService(getApplication())

    private val calendar = Calendar.getInstance()
    private lateinit var startDate: LocalDateTime

    lateinit var sessionID: String

    fun selectedActivity(activity: String) {
        _currentActivity.value = activity
    }

    fun startCapture() {
        startDate = LocalDateTime.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND),
        )

        Log.v("App", startDate.toString())

        sessionID = "ActivityDetectorVilhena" + "_" + startDate.toString() + "_" + UUID.randomUUID()
        sessionID = sessionID.replace("([T,:-])".toRegex(), "")

        Log.v("SessionID", sessionID)

        sensorCaptureService.registerSensors()
    }

    //  Stop capture sensor data
    fun stopCapture() {
        sensorCaptureService.unregisterSensors()
    }
}
