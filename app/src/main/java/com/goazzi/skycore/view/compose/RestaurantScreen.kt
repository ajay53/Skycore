package com.goazzi.skycore.view.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.goazzi.skycore.model.BusinessesServiceClass
import com.goazzi.skycore.viewmodel.MainViewModel

@Composable
fun RestaurantScreen(
    viewModel: MainViewModel,
//    onRadiusChanged: (Float) -> Unit,
//    onLocationSwitchChanged: (Boolean) -> Unit,
//    businessesServiceClass: BusinessesServiceClass,


    modifier: Modifier = Modifier
) {
//    var radius by remember { mutableFloatStateOf(100f) }
//    val isLocationSwitchEnabled = viewModel.isLocationSwitchEnabled.observeAsState()

    val businessesServiceClassState = viewModel.businessServiceClass.observeAsState()

    callApi(viewModel.radius.value.toInt(), viewModel.isLocationSwitchEnabled.value)

    Column {
//        Text(text = radius.toString())
        RestaurantSelectorScreen(
            onSliderPositionChanged = { radius ->
                viewModel.setRadius(radius)
//                callApi(radius.toInt(), viewModel.isLocationSwitchEnabled.value)
//                onRadiusChanged(radius)
//                onRestaurantSelectorUpdated(radius)
//                radius = sliderPosition
//                callApi(radius.toInt(), isLocationSwitchEnabled.value ?: false)
            },
            onLocationSwitchChanged = { isEnabled ->
                viewModel.setLocationSwitch(isEnabled)
//                onLocationSwitchChanged(isEnabled)
//                isLocationSwitchEnabled = isEnabled
//                onRestaurantSelectorUpdated(100, isEnabled)
//                viewModel._isLocationSwitchEnabled.value = selectedLocation
//                viewModel._radius = 100f
//                callApi(100f, isLocationSwitchEnabled.value ?: false)
//                locationSwitch.value = selectedLocation
            },
            modifier = modifier
        )
//        RestaurantListScreen(businessesServiceClass = businessesServiceClass)
    }
}

private fun callApi(radius: Int, isLocationSwitchEnabled: Boolean) {

}