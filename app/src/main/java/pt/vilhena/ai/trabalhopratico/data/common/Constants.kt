package pt.vilhena.ai.trabalhopratico.data.common

object Constants {
    const val TAG: String = "TPAi"
    const val SENSOR_CAPTURE_REFRESH_RATE: Int = 20
    const val AUTOMATIC_INTERVAL: Int = 5000
    const val GPS_INTERVAL: Long = 2000L
    const val CSV_HEADER: String = "session_id,lat,long,alt,accuracy,bearing,timestamp,x_acc,y_acc,z_acc,x_gyro,y_gyro,z_gyro,x_mag,y_mag,z_mag,activity"
    const val ARFF_HEADER: String = "@relation SensorData\n\n" +
        "@attribute alt numeric\n" +
        "@attribute accuracy numeric\n" +
        "@attribute bearing numeric\n" +
        "@attribute x_acc numeric\n" +
        "@attribute y_acc numeric\n" +
        "@attribute z_acc numeric\n" +
        "@attribute x_gyro numeric\n" +
        "@attribute y_gyro numeric\n" +
        "@attribute z_gyro numeric\n" +
        "@attribute x_mag numeric\n" +
        "@attribute y_mag numeric\n" +
        "@attribute z_mag numeric\n" +
        "@attribute activity {upstairs,walking,downstairs,standing}\n" +
        "@data\n"
}
