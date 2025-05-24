package com.farukayata.e_commerce2.ui.util

import android.os.AsyncTask
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import com.farukayata.e_commerce2.BuildConfig


class EmailSender {
    companion object {
        private const val EMAIL = BuildConfig.EMAIL_USER
        private const val PASSWORD = BuildConfig.EMAIL_PASS
    }

    fun sendReceiptEmail(
        recipientEmail: String,
        customerName: String,
        orderDate: String,
        totalPrice: String,
        items: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        SendEmailTask(
            recipientEmail,
            customerName,
            orderDate,
            totalPrice,
            items,
            onSuccess,
            onError
        ).execute()
    }

    private class SendEmailTask(
        private val recipientEmail: String,
        private val customerName: String,
        private val orderDate: String,
        private val totalPrice: String,
        private val items: List<String>,
        private val onSuccess: () -> Unit,
        private val onError: (String) -> Unit
    ) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val properties = Properties()
            properties["mail.smtp.host"] = "smtp.gmail.com"
            properties["mail.smtp.socketFactory.port"] = "465"
            properties["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            properties["mail.smtp.auth"] = "true"
            properties["mail.smtp.port"] = "465"

            try {
                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(EMAIL, PASSWORD)
                    }
                })

                val message = MimeMessage(session)
                message.setFrom(InternetAddress(EMAIL))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                message.subject = "Sipariş Fişi - $orderDate"

                val emailContent = buildString {
                    appendLine("Sayın $customerName,")
                    appendLine()
                    appendLine("Siparişiniz için teşekkür ederiz.")
                    appendLine("Sipariş Tarihi: $orderDate")
                    appendLine()
                    appendLine("Sipariş Detayları:")
                    items.forEach { appendLine(it) }
                    appendLine()
                    appendLine("Toplam Tutar: $totalPrice")
                    appendLine()
                    appendLine("Bizi tercih ettiğiniz için teşekkür ederiz.")
                }

                message.setText(emailContent)
                Transport.send(message)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        override fun onPostExecute(success: Boolean) {
            if (success) {
                onSuccess()
            } else {
                onError("E-posta gönderilirken bir hata oluştu.")
            }
        }
    }
}