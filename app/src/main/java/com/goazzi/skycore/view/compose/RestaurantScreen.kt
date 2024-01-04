package com.goazzi.skycore.view.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.goazzi.skycore.viewmodel.MainViewModel

@Composable
fun RestaurantScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    var radius by remember { mutableFloatStateOf(100f) }
    var locationSwitch by remember { mutableStateOf(false) }

    Column {
        Text(text = radius.toString())
        RestaurantSelectorScreen(
            onSliderPositionChanged = { sliderPosition ->
                radius = sliderPosition
            },
            onLocationSwitchChanged = { location ->
                locationSwitch == Enum.Location.CURRENT
            },
            modifier = modifier
        )
//        RestaurantListScreen(viewModel = )
    }

}