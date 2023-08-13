package pt.vilhena.ai.trabalhopratico.data.model

data class DocumentModel(
    val sessionID: String,
    val lat: Long,
    val long: Long,
    val altitude: Long,
    val speed: Long,
    val accuracy: Long,
    val bearing: Long,
    val timeStamp: Long,
    val xAcc: Long,
    val yAcc: Long,
    val zAcc: Long,
    val xGyro: Long,
    val yGyro: Long,
    val zGyro: Long,
    val activity: Long,
)
