package com.goazzi.skycore.view.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.goazzi.skycore.model.BusinessesServiceClass

@Composable
fun RestaurantScreen(
//    onRadiusChanged: (Float) -> Unit,
//    onLocationSwitchChanged: (Boolean) -> Unit,
//    businessesServiceClass: BusinessesServiceClass,


    viewModel:BusinessesServiceClass,
    modifier: Modifier = Modifier
) {
//    var radius by remember { mutableFloatStateOf(100f) }
//    val isLocationSwitchEnabled = viewModel.isLocationSwitchEnabled.observeAsState()

//    val businessesServiceClassState = viewModel.businessServiceClass.observeAsState()

    Column {
//        Text(text = radius.toString())
        RestaurantSelectorScreen(
            onSliderPositionChanged = { radius ->
                callApi(radius, viewModel.is)
                onRadiusChanged(radius)
//                onRestaurantSelectorUpdated(radius)
//                radius = sliderPosition
//                callApi(radius.toInt(), isLocationSwitchEnabled.value ?: false)
            },
            onLocationSwitchChanged = { isEnabled ->
                onLocationSwitchChanged(isEnabled)
//                isLocationSwitchEnabled = isEnabled
//                onRestaurantSelectorUpdated(100, isEnabled)
//                viewModel._isLocationSwitchEnabled.value = selectedLocation
//                callApi(radius.toInt(), isLocationSwitchEnabled.value ?: false)
//                locationSwitch.value = selectedLocation
            },
            modifier = modifier
        )
        RestaurantListScreen(businessesServiceClass = businessesServiceClass)
    }
}

private fun callApi(radius: Int, isLocationSwitchEnabled: Boolean) {

}