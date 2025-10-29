package br.com.sttsoft.ticktzy.extensions

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView

fun ImageView.setImageFromBase64(base64String: String) {
    try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        this.setImageBitmap(bitmap)
    } catch (e: Exception) {
        Log.e("EXT IMAGEVIEW", "setImageFromBase64: ", e)
    }
}
