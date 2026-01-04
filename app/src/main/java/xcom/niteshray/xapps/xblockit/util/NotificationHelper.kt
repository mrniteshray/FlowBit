package xcom.niteshray.xapps.xblockit.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import xcom.niteshray.xapps.xblockit.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "blockit_channel"
        const val CHANNEL_NAME = "FlowBit Alerts"
        const val NOTIFICATION_ID = 1001
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Used to show blocked alerts"
                enableLights(true)
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        context,
                        "Please enable notification permission for FlowBit",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun showBreakNotification() {
        checkAndRequestPermission()
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("FlowBit's On, Stay Focused! 🧘")
            .setContentText("")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setTimeoutAfter(10000)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    fun showBlockedNotification() {
        checkAndRequestPermission()
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(getMessages())
            .setContentText("")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setTimeoutAfter(10000)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    fun getMessages() : String{
        val messages = listOf(
            "Oops! This app is blocked to keep you focused. 👀",
            "Stay on track. Your goals are bigger than reels! 💪",
            "You’ve already seen enough today. Let’s refocus! ⏰",
            "Small actions = Big habits. Let’s grow strong 💡",
            "Take a breath. Your time is valuable. 🧘‍♂️",
            "FlowBit is helping you unplug for a while. 🔌",
            "This moment could be productive. Let’s use it well. 📚",
            "Winners stay focused. You’re one of them! 🏆",
            "One scroll less, one goal closer. Keep going! 🚀",
            "The future you will thank you for this pause. 🙏",
            "Every minute counts. Make it meaningful. ⏳",
            "Your brain deserves a break from endless scrolling. 🧠",
            "Control your screen, don’t let it control you. 🕹️",
            "Focus is the new superpower. You’ve got it! ⚡",
            "Distractions waste dreams. Stay sharp. ✨"
        )
        return messages.random()
    }
}