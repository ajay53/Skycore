package com.goazzi.skycore.view.compose

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goazzi.skycore.R
import com.goazzi.skycore.model.Business

@Composable
fun RestaurantListItem(business: Business? = null, modifier: Modifier = Modifier) {
    Surface(shape = MaterialTheme.shapes.small, modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier
                .height(60.dp)
                .background(color = colorResource(id = R.color.holo_orange_dark))
                .padding(5.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {

            business!! //non-null asserting for all the future use
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(business.imageURL)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_restaurant),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_restaurant),
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(color = Color.Transparent)
                    .size(50.dp)
            )

            /*Image(
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
            )*/

            Spacer(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
            )

            Column(
                verticalArrangement = Arrangement.SpaceAround, modifier = modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(color = colorResource(id = R.color.purple_200))
            ) {

                if (business.name != null && business.name.isNotBlank()) {
                    Text(
                        text = business.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
//                fontSize = 12.sp,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        /*modifier = Modifier.background(
                            color = colorResource(id = R.color.teal_200)
                        )*/
                    )
                }

                if (business.location?.address1 != null && business.location.address1.isNotBlank()) {
                    Text(
                        text = business.location.address1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        /*modifier = Modifier.background(
                            color = colorResource(id = R.color.teal_200)
                        )*/
                    )
                }

                //setting status text color as per the open/close status
                val status = if (business.isClosed?: false) {
                    "Currently CLOSED"
                } else {
                    "Currently OPEN"
                }

                val spannable = SpannableString(status)
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            LocalContext.current,
                            R.color.black
                        )
                    ),
                    0, 9,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (business.isClosed) {
                    spannable.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                LocalContext.current,
                                R.color.holo_red_dark
                            )
                        ),
                        9, status.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    spannable.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                LocalContext.current,
                                R.color.holo_green_dark
                            )
                        ),
                        9, status.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }


                Text(
                    text = spannable.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
//                fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall,
                    /*modifier = Modifier.background(
                        color = colorResource(id = R.color.teal_200)
                    )*/
                )
            }

            Spacer(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(color = colorResource(id = R.color.holo_green_dark))
            ) {
//                Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
                Text(
                    text = "3.5",
                    color = colorResource(id = R.color.white),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
//                            .fillMaxSize()
                        .align(Alignment.Center)
                )
//                }

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