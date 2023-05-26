package pt.vilhena.ai.trabalhopratico.data.model

data class DocumentModel(
    val sessionID: String,
    val lat: Long,
    val long: Long,
    val height: Long,
    val speed: Long,

    val startTime: Long,
    val activity: Long,
)
