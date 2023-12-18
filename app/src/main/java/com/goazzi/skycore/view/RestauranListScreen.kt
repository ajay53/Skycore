package com.goazzi.skycore.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChipDefaults.shape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goazzi.skycore.R
import com.goazzi.skycore.model.Business

@Composable
fun RestaurantListScreen() {
}

@Composable
fun RestaurantListItem(business: Business? = null, modifier: Modifier = Modifier) {
    Surface(shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Row(
            modifier = modifier
                .height(60.dp)
                .background(color = colorResource(id = R.color.holo_orange_dark))
                .padding(5.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Image(
                painter = painterResource(R.drawable.ic_restaurant),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
//                .size(50.dp)
                    .background(
                        color = colorResource(
                            id = R.color.holo_green_dark
                        )
                    )
                    .clip(CircleShape)
            )

            Column(
                verticalArrangement = Arrangement.SpaceAround, modifier = modifier
                    .fillMaxHeight()
                    .background(color = colorResource(id = R.color.purple_200))
            ) {
                Text(
                    text = "Restaurant Name",
//                fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.background(
                        color = colorResource(id = R.color.teal_200)
                    )
                )
                /*Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(color = colorResource(id = R.color.black))
                )*/
//            Text(text = "Address Line 1", style = MaterialTheme.typography.bodySmall)
                /*Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(color = colorResource(id = R.color.black))
                )*/
                Text(
                    text = "Address Line 2",
//                fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun RestaurantListItemPreview() {
    MaterialTheme {
        RestaurantListItem()
    }
}
