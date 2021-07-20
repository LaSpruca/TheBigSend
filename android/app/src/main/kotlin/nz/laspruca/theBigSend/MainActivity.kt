package nz.laspruca.theBigSend

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity : FlutterActivity() {
    private val CHANNEL = "theBigSend.laspruca.nz/sms"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "sendSMS") {
                val message = call.argument<String>("message")!!
                val numbers = call.argument<List<String>>("recipients")!!

                for (number in numbers) {
                    Log.i("SMS Thing", "Sending message to $number, with message $message")

                    sendSMS(number, message)
                }

                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        val sentPendingIntents = ArrayList<PendingIntent>()
        val deliveredPendingIntents = ArrayList<PendingIntent>()
        val sentPI = PendingIntent.getBroadcast(baseContext, 0,
                Intent(baseContext, SmsSentReceiver::class.java), 0)

        val deliveredPI = PendingIntent.getBroadcast(baseContext, 0,
                Intent(baseContext, SmsDeliveredReceiver::class.java), 0)
        try {
            val sms = SmsManager.getDefault()
            val mSMSMessage = sms.divideMessage(message)
            for (i in 0 until mSMSMessage.size) {
                sentPendingIntents.add(i, sentPI)
                deliveredPendingIntents.add(i, deliveredPI)
            }
            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
                    sentPendingIntents, deliveredPendingIntents)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(baseContext, "SMS sending failed...",
                    Toast.LENGTH_SHORT).show()
        }
    }

    private class SmsDeliveredReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (resultCode) {
                Activity.RESULT_OK -> Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show()
                Activity.RESULT_CANCELED -> Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private class SmsSentReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (resultCode) {
                RESULT_OK -> Toast.makeText(context,
                        "SMS Sent" + intent!!.getIntExtra("object", 0),
                        Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(context, "SMS generic failure", Toast.LENGTH_SHORT)
                        .show()
                SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(context, "SMS no service", Toast.LENGTH_SHORT)
                        .show()
                SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(context, "SMS null PDU", Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(context, "SMS radio off", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
