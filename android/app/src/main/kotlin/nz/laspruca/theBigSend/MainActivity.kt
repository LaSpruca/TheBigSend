package nz.laspruca.theBigSend

import android.app.PendingIntent
import android.content.Intent
import android.telephony.SmsManager
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
                sendSMS(message, numbers)
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun sendSMS(message: String, numbers: List<String>) {
        val sms = SmsManager.getDefault()
        val sentPI: PendingIntent
        val SENT = "SMS_SENT"

        sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), 0)

        for (phoneNumber in numbers) {
            sms.sendTextMessage(phoneNumber, null, message, sentPI, null)
        }
    }
}
