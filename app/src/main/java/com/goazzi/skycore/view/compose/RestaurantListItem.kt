package com.goazzi.skycore.view.compose

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.goazzi.skycore.R
import com.goazzi.skycore.model.Business

@Composable
fun RestaurantListItem(business: Business? = null, modifier: Modifier = Modifier) {
    Surface(shape = MaterialTheme.shapes.small, shadowElevation = 2.dp, modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier
                .height(60.dp)
                .background(color = Color.Transparent)
                .padding(5.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {

            business!! //non-null asserting for all the future use
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(business.imageURL)
                    .crossfade(true).build(),
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
                verticalArrangement = Arrangement.SpaceAround,
                modifier = modifier
                    .fillMaxHeight()
                    .weight(1f)
//                    .background(color = colorResource(id = R.color.purple_200))
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
                val status: String = if (business.isClosed ?: false) {
                    "Currently CLOSED"
                } else {
                    "Currently OPEN"
                }

                val builder: AnnotatedString.Builder = AnnotatedString.Builder()
                builder.append(status)

                val statusStyle = SpanStyle(
                    color = if (business.isClosed
                            ?: false
                    ) colorResource(id = R.color.holo_red_dark) else colorResource(id = R.color.holo_green_dark)
                )

                builder.addStyle(style = statusStyle, 9 + 1, status.length)

                Text(
                    text = builder.toAnnotatedString(),
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

            val rating: Double = business.rating ?: 0.0

            val colorId: Int = when {
                rating > 4.5 -> R.color.holo_green_dark
                rating > 4.0 -> R.color.yellow
                rating > 3.5 -> R.color.holo_orange_dark
                else -> R.color.holo_red_dark
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
                    .background(color = colorResource(id = colorId))
            ) {
//                Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
                Text(
                    text = business.rating.toString(),
                    color = colorResource(id = R.color.white),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
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