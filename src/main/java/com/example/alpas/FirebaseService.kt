package com.example.alpas

 import android.app.NotificationChannel
 import android.app.NotificationManager
 import android.app.PendingIntent
 import android.content.Context
 import android.content.Intent
 import android.content.SharedPreferences
 import android.graphics.Color
 import android.os.Build
 import android.util.Log
 import androidx.annotation.RequiresApi
 import androidx.core.app.NotificationCompat
 import com.example.alpas.models.PushNotification
 import com.example.alpas.ui.NotificationsFragment
 import com.example.alpas.utilsAlpas.RetrofitInstance
 import com.google.firebase.messaging.FirebaseMessagingService
 import com.google.firebase.messaging.RemoteMessage
 import com.google.gson.Gson
 import kotlinx.coroutines.CoroutineScope
 import kotlinx.coroutines.Dispatchers
 import kotlinx.coroutines.launch
 import kotlin.random.Random

class FirebaseService : FirebaseMessagingService(){
    val CHANNEL_ID = "Alpas notification channel"
    companion object{
        var sharedPref: SharedPreferences? = null
        var token:String?
            get(){
                return sharedPref?.getString("token", "")
            }
            set(value){
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
    }
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        var intent =Intent()
        intent = when(p0.data["type"]?.toInt()){
            0 -> Intent(this, MessageActivity::class.java)
            1,4,5 -> Intent(this, MyThreadsActivity::class.java)
            2 -> Intent(this, MySoldProductsActivity::class.java)
            3 -> Intent(this, MyOrderActivity::class.java)
            else -> Intent(this, DashboardActivity::class.java)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()
        createNotificationChannel(notificationManager)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(p0.data["title"])
            .setContentText(p0.data["message"])
            .setSmallIcon(R.drawable.alpas_logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "Alpas Chat Channel"
        val channel = NotificationChannel(
            CHANNEL_ID, channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description="Social Media for Engineering"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("TAG", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("TAG", response.errorBody()!!.string())
            }
        } catch (e: Exception) {
            Log.e("TAG", e.toString())
        }
    }
}