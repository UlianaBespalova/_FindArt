package com.skvoznyak.findart.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import com.skvoznyak.findart.ml.ModelMeta
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder


class TfliteModel {

    fun imageToVector(bm_s: Bitmap, context: Context) : FloatArray {

        var res = emptyArray<Float>().toFloatArray()
        val bm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bm_s.copy(Bitmap.Config.RGBA_F16, true)
        } else {
            return res
        }

        try{
            val model = ModelMeta.newInstance(context)

            val bm_8888: Bitmap = bm.copy(Bitmap.Config.ARGB_8888, true)
            val bm_sized_8888 = Bitmap.createScaledBitmap(bm_8888, 224, 224, true)

            val byteBuffer = convertBitmapToByteBuffer(bm_sized_8888)
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
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
        val INPUT_SIZE = 224
        val PIXEL_SIZE = 3

        val IMAGE_MEAN = 127.5
        val IMAGE_STD = 1

        val imgData = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE)
        imgData.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)

        imgData.rewind()
        bm.getPixels(intValues, 0, bm.width, 0, 0, bm.width, bm.height)

        var pixel = 0
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val value = intValues[pixel++]
                imgData.putFloat(((( value.shr(16) and 0xFF)/IMAGE_MEAN)-IMAGE_STD).toFloat())
                imgData.putFloat((((value.shr(8) and 0xFF)/IMAGE_MEAN)-IMAGE_STD).toFloat())
                imgData.putFloat((((value and 0xFF)/127.5)-1).toFloat())
            }
        }
        return imgData;
    }


    fun PRINT_RESUTL(res: FloatArray) {
        for (step in (0..3)) {
            Log.d("ivan", "---------------$step--------------")

            val vector = mutableListOf<Float>()
            for (i in (256*step..256*(step+1)-1)) {
                vector.add(res[i])
            }
            val str = vector.joinToString(separator = "|")
            Log.d("ivan", str)
        }
    }
}