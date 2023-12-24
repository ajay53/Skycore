package com.goazzi.skycore.view.compose

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.goazzi.skycore.model.Business
import com.goazzi.skycore.viewmodel.MainViewModel

@Composable
fun RestaurantListScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val foo = viewModel.businessServiceClass.observeAsState()
    val business: List<Business>? = foo.value?.businesses

    business?.let {
        LazyColumn(modifier = modifier.fillMaxHeight()) {
            this.items(
                count = business.size,
                key = { business[it].id },
                itemContent = { index ->
                    RestaurantListItem(business[index])
                }
            )
            /*items = business,
            key = { business: Business -> business.id }) { business: Business ->
            RestaurantListItem(business)*/
        }
    }
}


