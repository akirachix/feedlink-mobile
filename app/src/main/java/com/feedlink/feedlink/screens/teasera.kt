package com.example.feedlink.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.feedlink.feedlink.R
import com.feedlink.feedlink.ui.theme.Green

@Composable
fun WelcomeScreen(
    onSkipClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.top),
            contentDescription = "Top Green Curve",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(235.dp))

            Image(
                painter = painterResource(id = R.drawable.basket),
                contentDescription = "Basket",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IndicatorDot(active = true)
                Spacer(modifier = Modifier.width(10.dp))
                IndicatorDot(active = false)
                Spacer(modifier = Modifier.width(10.dp))
                IndicatorDot(active = false)
            }

            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = "Welcome",
                color = Green,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "To FeedLink Where food waste\nends and value begins.",
                color = Color.Black,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp, start = 30.dp, end = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Skip",
                    color = Green,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(onClick = onSkipClicked)
                )
                Text(
                    text = "Next",
                    color = Green,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(onClick = onNextClicked)
                )
            }
        }
    }
}

@Composable
fun IndicatorDot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(if (active) Color(0xFFFF9800) else Color(0xFFFAD9B3))
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(onSkipClicked = {}, onNextClicked = {})
}

