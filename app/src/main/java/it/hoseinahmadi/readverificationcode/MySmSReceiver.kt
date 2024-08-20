package it.hoseinahmadi.readverificationcode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.widget.Toast

class MySmSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        /* //Deprecated (Holo sen)
         if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
             val bundle = intent.extras as Bundle
             val format = bundle.getString("format")
             //PDus =Protocol Date Unit (S)
             val pdus = bundle.get("pdus") as Array<*>?
             if (pdus != null) {
                 var smsMessage = ""
                 val messages = mutableListOf<SmsMessage>()
                 pdus.forEachIndexed { index, any ->
                     messages.add(SmsMessage.createFromPdu(any as ByteArray, format))
                     smsMessage += "Sms From ${messages[index].originatingAddress} :" +
                             "${messages[index].messageBody}\n"
                 }
                 Toast.makeText(context, smsMessage, Toast.LENGTH_SHORT).show()
             }
         }*/

        //Ai
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                val format = bundle.getString("format")
                val pdus = bundle.getSerializable("pdus") as? Array<*>
                if (pdus != null) {
                    val messages = mutableListOf<SmsMessage>()
                    pdus.forEach { pdu ->
                        val message = SmsMessage.createFromPdu(pdu as ByteArray, format)
                        messages.add(message)
                    }

                    // ترکیب همه پیامک‌ها به یک متن واحد
                    val smsMessage = messages.joinToString("") { it.messageBody }

                    // استخراج کد تایید با استفاده از الگوی Regex مناسب
                    val codePattern = Regex("کد تایید شما:\\s*(\\d{4})")
                    val code = codePattern.find(smsMessage)?.groupValues?.get(1)
                      //                    کد تایید شما: 4545
                   //pattern
                    if (code != null) {
                        Toast.makeText(context, "Verification code: $code", Toast.LENGTH_SHORT)
                            .show()

                        // ارسال کد به اکتیویتی
                        val activityIntent = Intent().apply {
                            action = "service.to.activity.transfer"
                            putExtra("code", code)
                        }
                        context.sendBroadcast(activityIntent)
                    }
                }
            }
        }
    }
}







