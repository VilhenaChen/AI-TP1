package pt.vilhena.ai.trabalhopratico.data.file

import pt.vilhena.ai.trabalhopratico.data.common.Constants

data class FileModel(val fileName: String) {

    private var lines = ArrayList<String>()

    init {
        lines.add(Constants.CSV_HEADER)
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
        val line =
            "$sessionID,$lat,$long,$altitude,$accuracy,$bearing,$timeStamp,$speed,$xAcc,$yAcc,$zAcc,$xGyro,$yGyro,$zGyro,$xMag,$yMag,$zMag,$activity"
        lines.add(line)
    }

    //  getLines mapped to a ListOf<String> needed for the CSV
    fun getLines(): List<List<String>> {
        return lines.map { listOf(it) }
    }
}
