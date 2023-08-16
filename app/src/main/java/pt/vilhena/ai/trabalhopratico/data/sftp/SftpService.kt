package pt.vilhena.ai.trabalhopratico.data.sftp

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import pt.vilhena.ai.trabalhopratico.BuildConfig
import java.io.File
import java.util.Properties

class SftpService {
    private val hostname = BuildConfig.SERVER_HOSTNAME
    private val username = BuildConfig.SERVER_USERNAME
    private val password = BuildConfig.SERVER_PASSWORD
    private val port = 22
    private val path = "/home/amistudent/data/profile1/265"

    suspend fun copyFileToSftp(file: File): Boolean {
        var jschSession: Session? = null
        try {
            val jsch = JSch()
            jschSession = jsch.getSession(username, hostname, port)
            jschSession.setPassword(password)
            val config = Properties()
            config["StrictHostKeyChecking"] = "no"
            jschSession.setConfig(config)

            jschSession.connect(1000)
            val sftp: Channel = jschSession.openChannel("sftp")

            sftp.connect(5000)
            val channelSftp: ChannelSftp = sftp as ChannelSftp

            channelSftp.put(file.absolutePath, path)
            channelSftp.exit()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
}
