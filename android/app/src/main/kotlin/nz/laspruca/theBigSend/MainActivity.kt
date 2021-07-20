package nz.laspruca.theBigSend

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.lang.IllegalArgumentException


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
        val sent = "SMS_SENT"
        val delivered = "SMS_DELIVERED"

        // Create a listener for when the sms messages are sent
        val sentPI = PendingIntent.getBroadcast(this, 0,
                Intent(sent), 0)

        // Create a listner for when the SMS
        val deliveredPI = PendingIntent.getBroadcast(this, 0,
                Intent(delivered), 0)

        // When the message has been sent
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        Toast.makeText(baseContext, "Generic failure sending message",
                                Toast.LENGTH_SHORT).show()
                    }
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(baseContext, "No service",
                            Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(baseContext, "Null PDU",
                            Toast.LENGTH_SHORT).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(baseContext, "Radio off",
                            Toast.LENGTH_SHORT).show()
                    else -> {
                    }
                }
            }
        }, IntentFilter(sent))

        // When the sms message was received
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                    }
                    Activity.RESULT_CANCELED -> Toast.makeText(baseContext, "SMS not delivered",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }, IntentFilter(delivered))

        // Get the SMSManager to send messages with
        val sms = SmsManager.getDefault()
        try {
            // Send a message
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI)
        } catch (ignored: IllegalArgumentException) {
            // If it did not work
            Toast.makeText(applicationContext, "Bad phone number $phoneNumber", Toast.LENGTH_LONG).show()
        }
    }

}
