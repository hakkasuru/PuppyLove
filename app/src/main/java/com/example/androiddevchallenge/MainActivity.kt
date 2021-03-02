/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.models.Puppy
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val threshold = 250f
    private val velocityX = 0.025f
    private val velocityY = 0.025f

    private val puppies = listOf(
        Puppy(
            "Johnny Silverhand",
            3,
            "Husky",
            "Wake the **** up, Samurai! We have a city to burn",
            listOf("Video Games", "Sunglasses"),
            R.drawable.husky,
            true
        ),
        Puppy(
            "Jackie Welles",
            4,
            "Bulldog",
            "I'm a hustler, that's all you need to know about me'",
            listOf("Guns", "Money"),
            R.drawable.bulldog,
            false
        ),
        Puppy(
            "Panam Palmer",
            2,
            "German Shepherd",
            "I have a reputation",
            listOf("Nomad Life", "Racing"),
            R.drawable.german_shepherd,
            true
        )
    )

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MainView()
            }
        }
    }

    @ExperimentalMaterialApi
    @Preview
    @Composable
    private fun MainView() {
        Column {
            PuppyCardStack(puppies = puppies)
            SwipeButtons()
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun PuppyCardStack(puppies: List<Puppy>) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.85f)
                .padding(8.dp)
        ) {
            puppies.asReversed().forEachIndexed { index, puppy ->
                Log.d(TAG, "Card Index: $index")
                PuppyCard(puppy = puppy)
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun PuppyCard(puppy: Puppy) {
        val cornerSize = CornerSize(8.dp)
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
                }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        Log.d(TAG, "Offset: $offsetX, Delta: $delta")
                        offsetX += delta
                        if (offsetX > threshold) {
                            val rightAnchor = (offsetX + 500f)
                            while (offsetX < rightAnchor) {
                                offsetX += velocityX
                                offsetY += -velocityY
                            }
                        }
                        if (offsetX < -threshold) {
                            val leftAnchor = offsetX + -500f
                            while (offsetX > leftAnchor) {
                                offsetX += -velocityX
                                offsetY += -velocityY
                            }
                        }
                    }
                )
        ) {
            Card(
                shape = RoundedCornerShape(cornerSize, cornerSize, cornerSize, cornerSize),
                elevation = 4.dp,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(contentAlignment = Alignment.BottomStart) {
                    Image(
                        painter = painterResource(id = puppy.image),
                        contentDescription = "Image of puppy",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                    PuppyCardContent(puppy = puppy)
                }
            }
        }
    }

    @Composable
    private fun PuppyCardContent(puppy: Puppy) {
        val title = puppy.name + ", " + puppy.age.toString()
        val likes = "I love " + puppy.likes.joinToString(", ")

        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            PuppyCardContentTitle(title, puppy.verified)
            PuppyCardContentSubtitle(puppy.breed)
            PuppyCardContentText(puppy.bio)
//            PuppyCardContentText(likes)
        }
    }

    @Composable
    private fun PuppyCardContentTitle(text: String, verified: Boolean = false) {
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.h5,
                color = Color.White,
                modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
            )
            if (verified) {
                Image(
                    painter = painterResource(id = R.drawable.verified),
                    contentDescription = "This user is verified",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                )
            }
        }
    }

    @Composable
    private fun PuppyCardContentSubtitle(text: String) {
        Text(
            text = text,
            modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 0.dp),
            style = MaterialTheme.typography.h6,
            color = Color.White
        )
    }

    @Composable
    private fun PuppyCardContentText(text: String) {
        Text(
            text = text,
            modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 0.dp),
            style = MaterialTheme.typography.subtitle1,
            color = Color.White
        )
    }

    @Composable
    private fun SwipeButtons() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(0.dp, 8.dp, 0.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DislikeButton()
            LikeButton()
        }
    }

    @Composable
    private fun LikeButton() {
        FloatingActionButton(
            onClick = { onSwipeRight() },
            backgroundColor = Color.White,
        ) {
            Icon(
                imageVector = Icons.Filled.ThumbUp,
                tint = Color.Green,
                contentDescription = "Like Button"
            )
        }
    }

    @Composable
    private fun DislikeButton() {
        FloatingActionButton(
            onClick = { onSwipeLeft() },
            backgroundColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Filled.ThumbDown,
                tint = Color.Red,
                contentDescription = "Dislike Button"
            )
        }
    }

    private fun onSwipeRight() {
        // no-op
    }

    private fun onSwipeLeft() {
        // no-op
    }

    companion object {
        private const val TAG = "PuppyActivity"
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        Text(text = "Ready... Set... GO!")
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
