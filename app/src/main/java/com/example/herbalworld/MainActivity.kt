package com.example.herbalworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.herbalworld.ui.theme.HerbalWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HerbalWorldTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HerbalWorldTheme {
        Greeting("Android")
    }
}
//# Final-Year-Project
//## Exploring the domain of Image Processing
//Hello , This is the repository of our final year project on the domain of Image Processing.
//Our project is focused on developing an Android application that utilizes image processing techniques to identify and provide information on various medicinal leaves. The application utilizes a smartphone's camera to capture an image of a medicinal leaf, and then applies image processing algorithms to extract relevant features and identify the species of the plant.
//
//## Aim
//- The overarching goal of our project is to push the boundaries of image processing by harnessing the power of Android technology.
//- By harnessing the power of modern libraries like **Tensorflow**, we aim to delve deeper into the fascinating world
//of digital image manipulation, exploring new techniques.
//- Its still a work in progress 🚧 .
//
//## TODO
//
//- Display list of medicinal leaves and their importance in the home screen of the application
//- Display Details of the medicinal leaf in the detail screen when clicked
//- Integrate Image Processing to be able to recognize a medicinal leaf
//- Work on the UI of the app
//
//## Members
//- Manoj Kumar N
//- [Shashank shetty](https://github.com/shashank-0-0)
//- satish k