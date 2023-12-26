package com.goazzi.skycore.view.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goazzi.skycore.R
import com.goazzi.skycore.model.Business
import com.goazzi.skycore.viewmodel.MainViewModel

@Composable
fun RestaurantListScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val foo = viewModel.businessServiceClass.observeAsState()
    val business: List<Business>? = foo.value?.businesses

    //further checks can be added for http_status, etc.
    if (!business.isNullOrEmpty()) {
        //show restaurant list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = modifier
                .fillMaxHeight()
                .padding(horizontal = 5.dp)
        ) {
            this.items(
                count = business.size,
                key = { business[it].id },
                itemContent = { index ->
                    RestaurantListItem(business[index])
                }
            )
        }
    } else {
        //show no restaurant found layout
        NoRestaurantScreen()
    }
}

@Composable
fun NoRestaurantScreen(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painterResource(id = R.drawable.ic_restaurant),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = Color.Gray, blendMode = BlendMode.Modulate),
            modifier = Modifier.size(150.dp)
        )
        Text(
            text = stringResource(id = R.string.no_restaurants_increase_radius),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
//            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
//@Preview(showBackground = true)
fun NoRestaurantScreenPreview() {
    NoRestaurantScreen()
}

