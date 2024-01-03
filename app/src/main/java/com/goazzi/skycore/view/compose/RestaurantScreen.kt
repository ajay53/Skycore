package com.goazzi.skycore.view.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun RestaurantScreen() {
    var radius by remember { mutableFloatStateOf(100f) }
    var locationSwitch by remember { mutableStateOf(false) }

    Column {
        RestaurantSelectorScreen(onSliderPositionChanged = { sliderPosition ->

        })
//        RestaurantListScreen(viewModel = )
    }

}