package com.goazzi.skycore.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goazzi.skycore.R
import com.goazzi.skycore.model.Business

@Composable
fun RestaurantListScreen() {
}

@Composable
fun RestaurantListItem(business: Business? = null, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(color = colorResource(id = R.color.holo_orange_dark))
            .padding(0.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_restaurant),
            contentDescription = null,
            modifier = Modifier.background(
                color = colorResource(
                    id = R.color.holo_green_dark
                )
            )
        )

        val arrangement = Arrangement.apply {
            SpaceAround
            spacedBy(2.dp)
        }

        val arrangement2 = Arrangement.spacedBy(2.dp, alignment = Alignment.Start)

        Column(verticalArrangement = Arrangement.SpaceAround) {
            Text(
                text = "Restaurant Name",
                modifier = Modifier.background(color = colorResource(id = R.color.teal_200))
            )
            Text(text = "Address Line 1")
            Text(text = "Address Line 2")
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
