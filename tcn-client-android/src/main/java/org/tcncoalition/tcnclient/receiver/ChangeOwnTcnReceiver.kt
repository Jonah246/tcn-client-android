package org.tcncoalition.tcnclient.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import org.tcncoalition.tcnclient.TcnConstants.WAKELOCK_DURATION
import org.tcncoalition.tcnclient.TcnConstants.WAKELOCK_TAG
import org.tcncoalition.tcnclient.bluetooth.TcnBluetoothService
import org.tcncoalition.tcnclient.bluetooth.TcnBluetoothService.LocalBinder


class ChangeOwnTcnReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val pm = it.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)
            wl.acquire(WAKELOCK_DURATION.toLong())

            val binder: IBinder? = peekService(it, Intent(context, TcnBluetoothService::class.java))
            if (binder != null) {
                val service: TcnBluetoothService = (binder as LocalBinder).service
                service.changeOwnTcn()
            } else {
                // Can happen if called before bindService is called
                Log.v(ChangeOwnTcnReceiver::javaClass.name, "Binder is null")
            }

            wl.release()
        }
    }

    companion object {
        private const val REQUEST_CODE = 42

        fun pendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, ChangeOwnTcnReceiver::class.java)
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)

            return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}