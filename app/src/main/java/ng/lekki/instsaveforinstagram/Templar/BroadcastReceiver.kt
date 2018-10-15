package ng.lekki.instsaveforinstagram.Templar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat

class OnBoot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val serviceIntent = Intent(context, Graham::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}