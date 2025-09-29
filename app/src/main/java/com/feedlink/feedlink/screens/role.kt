package com.feedlink.feedlink.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.feedlink.feedlink.R


@Composable
fun WelcomeRoleScreen(
    onCustomerClick: () -> Unit = {},
    onRecyclerClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF045611))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.orange),
                contentDescription = "FeedLink Logo",
                modifier = Modifier.size(250.dp)
                    .offset(x = 30.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick =  onCustomerClick,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text = "Buyer", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(26.dp))

            OutlinedButton(
                onClick = onRecyclerClick,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(52.dp),
                border = BorderStroke(1.dp, Color(0xFFFF9800)),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFFFF9800)
                )
            ) {
                Text(text = "Recycler", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
            }
        }



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeRoleScreenPreview() {
    WelcomeRoleScreen()
}