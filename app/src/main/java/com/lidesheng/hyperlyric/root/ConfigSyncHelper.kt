package com.lidesheng.hyperlyric.root

import com.lidesheng.hyperlyric.*

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ConfigSyncHelper {

    private const val SYSTEM_CONFIG_PATH = "/data/system/hyperlyric.conf"

    suspend fun syncFullConfigToSystem(context: Context) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

        val configString = with(prefs) {
            val size = getInt(Constants.KEY_TEXT_SIZE, Constants.DEFAULT_TEXT_SIZE)
            val marquee = getBoolean(Constants.KEY_MARQUEE_MODE, true)
            val hideNotch = getBoolean(Constants.KEY_HIDE_NOTCH, false)
            val maxLeftWidth = getInt(Constants.KEY_MAX_LEFT_WIDTH, Constants.DEFAULT_MAX_LEFT_WIDTH)
            val speed = getInt(Constants.KEY_MARQUEE_SPEED, Constants.DEFAULT_MARQUEE_SPEED)
            val delay = getInt(Constants.KEY_MARQUEE_DELAY, Constants.DEFAULT_MARQUEE_DELAY)
            val animMode = getInt(Constants.KEY_ANIM_MODE, Constants.DEFAULT_ANIM_MODE)

            val whitelistStr = getStringSet(Constants.KEY_WHITELIST, emptySet())
                ?.joinToString(",") ?: ""

            "size=$size;marquee=$marquee;hideNotch=$hideNotch;maxLeftWidth=$maxLeftWidth;" +
                    "speed=$speed;delay=$delay;animMode=$animMode;whitelist=$whitelistStr"
        }

        val tempFile = File(context.cacheDir, "temp_config.conf")
        try {
            tempFile.writeText(configString)

            val cmd = "cp -f ${tempFile.absolutePath} $SYSTEM_CONFIG_PATH && chmod 644 $SYSTEM_CONFIG_PATH"
            ShellUtils.execRootCmdSilent(cmd)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            tempFile.delete()
        }
    }
}
