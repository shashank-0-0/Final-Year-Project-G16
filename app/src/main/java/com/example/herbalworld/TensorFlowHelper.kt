package com.example.herbalworld


import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.platform.LocalContext
import com.example.herbalworld.ml.ModelUnquant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


object TensorFLowHelper {

    @Composable
    fun classifyImage1(image: Bitmap, callback: @Composable (fruit: String) -> Unit) {
        val model: ModelUnquant = ModelUnquant.newInstance(LocalContext.current)

        val inputWidth = 224
        val inputHeight = 224
        val inputChannels = 3

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, inputWidth, inputHeight, inputChannels)
            , DataType.FLOAT32)

        val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * inputWidth * inputHeight * inputChannels)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputWidth * inputHeight)

        image.getPixels(intValues, 0, inputWidth, 0, 0, inputWidth, inputHeight)

        for (pixelValue in intValues) {
            byteBuffer.putFloat((pixelValue shr 16 and 0xFF) * (1f / 255f))
            byteBuffer.putFloat((pixelValue shr 8 and 0xFF) * (1f / 255f))
            byteBuffer.putFloat((pixelValue and 0xFF) * (1f / 255f))
        }
        byteBuffer.rewind() // Reset the bu ffer position to 0 before loading into TensorBuffer
        inputFeature0.loadBuffer(byteBuffer)

        val outputs: ModelUnquant.Outputs = model.process(inputFeature0)
        val outputFeature0: TensorBuffer = outputs.getOutputFeature0AsTensorBuffer()

        val confidences = outputFeature0.floatArray
        var maxPos = 0
        var maxConfidence = 0f
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }

        val classes = arrayOf("Tulsi", "Curry", "Mint", "Lemon", "Basale", "Cactus", "Aloe Vera")
        Log.i("shetty","$maxPos $maxConfidence")
        callback.invoke(classes[maxPos])

        model.close()
    }
}