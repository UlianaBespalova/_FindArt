package com.skvoznyak.findart.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import com.skvoznyak.findart.ml.ModelMeta
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class TfliteModel {

    fun imageToVector(bmNew: Bitmap, context: Context): FloatArray {

        var res = emptyArray<Float>().toFloatArray()
        val bm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bmNew.copy(Bitmap.Config.RGBA_F16, true)
        } else {
            return res
        }

        try {
            val model = ModelMeta.newInstance(context)

            val bm8888: Bitmap = bm.copy(Bitmap.Config.ARGB_8888, true)
            val bmSized8888 = Bitmap.createScaledBitmap(bm8888, 224, 224, true)

            val byteBuffer = convertBitmapToByteBuffer(bmSized8888)
            val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, 224, 224, 3),
                DataType.FLOAT32
            )
            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            res = outputFeature0.floatArray
            model.close()
        } catch (e: Exception) {
            Log.d("ivan", "Error while loading magic")
            e.printStackTrace()
        }
        return res
    }

    private fun convertBitmapToByteBuffer(bm: Bitmap): ByteBuffer {
        val inputSize = 224
        val pixelSize = 3

        val imageMean = 127.5
        val imageStd = 1

        val imgData = ByteBuffer.allocateDirect(4 * inputSize * inputSize * pixelSize)
        imgData.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)

        imgData.rewind()
        bm.getPixels(intValues, 0, bm.width, 0, 0, bm.width, bm.height)

        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]
                imgData.putFloat((((value.shr(16) and 0xFF) / imageMean) - imageStd).toFloat())
                imgData.putFloat((((value.shr(8) and 0xFF) / imageMean) - imageStd).toFloat())
                imgData.putFloat((((value and 0xFF) / 127.5) - 1).toFloat())
            }
        }
        return imgData
    }
}
