package com.example.herbalworld.UiLayer.search

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.herbalworld.TensorFLowHelper
import com.example.herbalworld.ml.ModelUnquant
import com.example.herbalworld.ui.theme.HerbalWorldTheme
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.random.Random


@Composable
fun SearchScreen(
    onCameraClick:()->Unit,
    onGalleryClick:()->Unit
) {
    val context = LocalContext.current


    var output by remember{
        mutableStateOf<String>("")
    }
    var showDialog = remember{
        mutableStateOf(false)
    }


    var image by remember { mutableStateOf<Bitmap?>(null) }

    if(showDialog.value){

            TensorFLowHelper.classifyImage1(image = image!!) {
                Log.i("shetty", "dont give up $it")
                output = it
                showDialog.value = false
            }

    }
    HerbalWorldTheme {


        val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            image = it
        }

        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            try {
                uri?.let {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    image = bitmap
                }

            } catch (e: IOException) {
                // Handle the error
            }
        }
        val requestPermissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Log.i("shetty","granted perission")
                    val result = saveBitmapToGallery(context, image!!)
                    if(result){
                        Toast.makeText(context,"Image Saved Sucessfully",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(context,"Image Not Saved",Toast.LENGTH_LONG).show()
                    }


                } else {
                    // Permission denied, handle accordingly
                    Log.i("shetty","not granted perission")
                    Toast.makeText(context,"Permission Denied",Toast.LENGTH_LONG).show()
                }
            }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Black, Color.DarkGray)))
                .padding(10.dp)
        ){
            Column(
                modifier = Modifier.align(Alignment.TopCenter)
            ) {

                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Upload Image",
                    fontSize = 25.sp,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row {
                    Button(onClick = {
                        cameraLauncher.launch()
                    }) {
                        Text("Camera")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(onClick = {
                        galleryLauncher.launch("image/*")
                    }) {
                        Text("Gallery")
                    }
                }
            }

            Column(
                modifier=Modifier.align(Alignment.Center),
            ){
                Spacer(modifier = Modifier.height(10.dp))

                image?.let {

                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Image of a plant",
                        modifier = Modifier.size(width = 250.dp, height = 200.dp)
                            .clickable {
                                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            },
                        contentScale = ContentScale.Fit,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            showDialog.value=true
                    }) {
                        Text("Detect Plant")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if(output.isNotEmpty()){
                        Text(
                            text = output,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontSize = 25.sp,
                            color = Color.White
                        )
                    }else{
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Apply Filters",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontSize = 25.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                image=BrightenImage(image!!,40)
                            }) {
                            Text("Bright Image")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                image = applyReflection(image!!)
                            }) {
                            Text("Reflect Image" )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                image= roundCornerImage(image!!,70F)
                            }) {
                            Text("Round Corner")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

fun BrightenImage(src: Bitmap, value: Int): Bitmap? {
    // image size
    val width = src.width
    val height = src.height
    // create output bitmap
    val bmOut = Bitmap.createBitmap(width, height, src.config)
    // color information
    var A: Int
    var R: Int
    var G: Int
    var B: Int
    var pixel: Int

    // scan through all pixels
    for (x in 0 until width) {
        for (y in 0 until height) {
            // get pixel color
            pixel = src.getPixel(x, y)
            A = android.graphics.Color.alpha(pixel)
            R = android.graphics.Color.red(pixel)
            G = android.graphics.Color.green(pixel)
            B = android.graphics.Color.blue(pixel)

            // increase/decrease each channel
            R += value
            if (R > 255) {
                R = 255
            } else if (R < 0) {
                R = 0
            }
            G += value
            if (G > 255) {
                G = 255
            } else if (G < 0) {
                G = 0
            }
            B += value
            if (B > 255) {
                B = 255
            } else if (B < 0) {
                B = 0
            }

            // apply new pixel color to output bitmap
            bmOut.setPixel(x, y, android.graphics.Color.argb(A, R, G, B))
        }
    }

    // return final image
    return bmOut
}

fun roundCornerImage(src: Bitmap, round: Float): Bitmap? {
    // image size
    val width = src.width
    val height = src.height
    // create bitmap output
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    // set canvas for painting
    val canvas = Canvas(result)
    canvas.drawARGB(0, 0, 0, 0)

    // config paint
    val paint = Paint()
    paint.setAntiAlias(true)
    paint.setColor(android.graphics.Color.BLACK)

    // config rectangle for embedding
    val rect = Rect(0, 0, width, height)
    val rectF = RectF(rect)

    // draw rect to canvas
    canvas.drawRoundRect(rectF, round, round, paint)

    // create Xfer mode
    paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
    // draw source image to canvas
    canvas.drawBitmap(src, rect, rect, paint)
    // return final image
    return result
}
fun applyReflection(originalImage: Bitmap): Bitmap? {
    // gap space between original and reflected
    val reflectionGap = 4
    // get image size
    val width = originalImage.width
    val height = originalImage.height

    // this will not scale but will flip on the Y axis
    val matrix = Matrix()
    matrix.preScale(1F, -1F)

    // create a Bitmap with the flip matrix applied to it.
    // we only want the bottom half of the image
    val reflectionImage =
        Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false)

    // create a new bitmap with same width but taller to fit reflection
    val bitmapWithReflection = Bitmap.createBitmap(width, height + height / 2, Bitmap.Config.ARGB_8888)

    // create a new Canvas with the bitmap that's big enough for
    // the image plus gap plus reflection
    val canvas = Canvas(bitmapWithReflection)
    // draw in the original image
    canvas.drawBitmap(originalImage, 0f, 0f, null)
    // draw in the gap
    val defaultPaint = Paint()
    canvas.drawRect(
        0f,
        height.toFloat(),
        width.toFloat(),
        (height + reflectionGap).toFloat(),
        defaultPaint
    )
    // draw in the reflection
    canvas.drawBitmap(reflectionImage, 0f, (height + reflectionGap).toFloat(), null)

    // create a shader that is a linear gradient that covers the reflection
    val paint = Paint()
    val shader = android.graphics.LinearGradient(
        0F, originalImage.height.toFloat(), 0F,
        bitmapWithReflection.height.toFloat() + reflectionGap, 0x70ffffff, 0x00ffffff,
        Shader.TileMode.CLAMP
    )
    // set the paint to use this shader (linear gradient)
    paint.shader = shader
    // set the Transfer mode to be porter duff and destination in
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    // draw a rectangle using the paint with our linear gradient
    canvas.drawRect(
        0f,
        height.toFloat(),
        width.toFloat(),
        (bitmapWithReflection.height + reflectionGap).toFloat(),
        paint
    )
    return bitmapWithReflection
}


private fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {

    val imageFileName = ""+Random.nextInt()+"image.jpg"

    // Get the directory to save the image
    val directory =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val imageFile = File(directory, imageFileName)

    try {
        val outputStream: OutputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        // Notify the system to scan the saved image file so it appears in the gallery
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(imageFile)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)

        return true
    } catch (e: IOException) {
        e.printStackTrace()
        Log.i("shetty","${e.localizedMessage}")
    }

    return false
}
