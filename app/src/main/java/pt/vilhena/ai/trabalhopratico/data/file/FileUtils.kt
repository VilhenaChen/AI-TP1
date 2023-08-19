package pt.vilhena.ai.trabalhopratico.data.file

import android.os.Environment
import android.util.Log
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import pt.vilhena.ai.trabalhopratico.data.common.Constants
import pt.vilhena.ai.trabalhopratico.data.sftp.SftpService
import java.io.File

class FileUtils {

    private var fileName = ""
    private lateinit var fileModel: FileModel
    private lateinit var dataFile: File
    private val sftpService = SftpService()

    //  This is the documents folder path, to where the app will write the CSVs
    private val path =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

    //  Create FileModel and FileName
    fun createFileModel(sessionID: String, currentActivity: String) {
        fileName = "${sessionID}_$currentActivity.csv"
        fileModel = FileModel(fileName)
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
        speed: String,
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
            speed,
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

    /*  Write on the CSV file the data
        The First line is being remove since the sensors are still being started
     */
    suspend fun writeFile() {
        if (!path.exists()) {
            path.mkdir()
        }
        dataFile = File(path, fileName)
        csvWriter().writeAll(fileModel.getLines(), dataFile)
        sftpService.copyFileToSftp(dataFile)
    }

    //  Delete CSV file when uploads ends
    fun deleteLocalFile() {
        if (dataFile.exists()) {
            dataFile.delete()
        } else {
            Log.d(Constants.TAG, "File ${dataFile.name} does not exist")
        }
    }
}
