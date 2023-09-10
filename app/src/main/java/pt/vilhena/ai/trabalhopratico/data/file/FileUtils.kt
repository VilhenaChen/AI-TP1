package pt.vilhena.ai.trabalhopratico.data.file

import android.os.Environment
import android.util.Log
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import pt.vilhena.ai.trabalhopratico.data.common.Constants
import pt.vilhena.ai.trabalhopratico.data.sftp.SftpService
import java.io.File

class FileUtils(private val isAutomatic: Boolean) {

    private var fileName = ""
    private lateinit var fileModel: FileModel
    private lateinit var dataFile: File
    private val sftpService = SftpService()

    //  This is the documents folder path, to where the app will write the CSVs
    private val path =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

    //  Create FileModel and FileName
    fun createFileModel(sessionID: String, currentActivity: String) {
        fileName = if (isAutomatic) {
            "test.arff"
        } else {
            "${sessionID}_$currentActivity.csv"
        }
        fileModel = FileModel(fileName, isAutomatic)
    }

    //  Add new row for the csv to write
    fun addRow(
        sessionID: String,
        lat: String,
        long: String,
        altitude: String,
        accuracy: String,
        bearing: String,
        timeStamp: String,
        xAcc: String,
        yAcc: String,
        zAcc: String,
        xGyro: String,
        yGyro: String,
        zGyro: String,
        xMag: String,
        yMag: String,
        zMag: String,
        activity: String,
    ) {
        fileModel.addRow(
            sessionID,
            lat,
            long,
            altitude,
            accuracy,
            bearing,
            timeStamp,
            xAcc,
            yAcc,
            zAcc,
            xGyro,
            yGyro,
            zGyro,
            xMag,
            yMag,
            zMag,
            activity,
        )
    }

    fun addRowAuto(
        altitude: String,
        accuracy: String,
        bearing: String,
        xAcc: String,
        yAcc: String,
        zAcc: String,
        xGyro: String,
        yGyro: String,
        zGyro: String,
        xMag: String,
        yMag: String,
        zMag: String,
    ) {
        fileModel.addRow(
            altitude,
            accuracy,
            bearing,
            xAcc,
            yAcc,
            zAcc,
            xGyro,
            yGyro,
            zGyro,
            xMag,
            yMag,
            zMag,
        )
    }

    /*  Write on the CSV file the data
        The First line is being remove since the sensors are still being started
     */
    suspend fun writeFile(): Boolean {
        if (!path.exists()) {
            path.mkdir()
        }
        dataFile = File(path, fileName)
        csvWriter().writeAll(fileModel.getLines(), dataFile)
        return sftpService.copyFileToSftp(dataFile)
    }

    //  Delete CSV file when uploads ends
    fun deleteLocalFile() {
        if (dataFile.exists()) {
            dataFile.delete()
        } else {
            Log.d(Constants.TAG, "File ${dataFile.name} does not exist")
        }
    }

    //  Remove the prefix and Suffix of the last line and return it for Weka to classify
//    fun prepLastLineForWeka(): String {
//        val stringBuild = StringBuilder()
//        fileModel.getLastLine().map { line ->
//            stringBuild.append(line.toString().removePrefix("[").removeSuffix("]"))
//        }
//        return stringBuild.toString()
//    }

    fun prepLastLineForWeka(): String {
        val linesList = fileModel.getLines()
        fileModel.removeLines()
        var altAverage: Double = 0.0
        var accuracyAverage: Double = 0.0
        var bearingAverage: Double = 0.0
        var xAccAverage: Double = 0.0
        var yAccAverage: Double = 0.0
        var zAccAverage: Double = 0.0
        var xGyroAverage: Double = 0.0
        var yGyroAverage: Double = 0.0
        var zGyroAverage: Double = 0.0
        var xMagAverage: Double = 0.0
        var yMagAverage: Double = 0.0
        var zMagAverage: Double = 0.0
        Log.d("ff", linesList.toString())
        linesList.forEach {
            val line = it.toString().removePrefix("[").removeSuffix("]")
            val values = line.split(",")
            altAverage += values[0].toDouble()
            accuracyAverage += values[1].toDouble()
            bearingAverage += values[2].toDouble()
            xAccAverage += values[3].toDouble()
            yAccAverage += values[4].toDouble()
            zAccAverage += values[5].toDouble()
            xGyroAverage += values[6].toDouble()
            yGyroAverage += values[7].toDouble()
            zGyroAverage += values[8].toDouble()
            xMagAverage += values[9].toDouble()
            yMagAverage += values[10].toDouble()
            zMagAverage += values[11].toDouble()
        }
        altAverage /= 250
        accuracyAverage /= 250
        bearingAverage /= 250
        xAccAverage /= 250
        yAccAverage /= 250
        zAccAverage /= 250
        xGyroAverage /= 250
        yGyroAverage /= 250
        zGyroAverage /= 250
        xMagAverage /= 250
        yMagAverage /= 250
        zMagAverage /= 250
        return "$altAverage,$accuracyAverage,$bearingAverage,$xAccAverage,$yAccAverage,$zAccAverage,$xGyroAverage,$yGyroAverage,$zGyroAverage,$xMagAverage,$yMagAverage,$zMagAverage"
    }
}
