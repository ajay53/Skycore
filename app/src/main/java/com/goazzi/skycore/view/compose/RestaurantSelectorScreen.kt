package com.goazzi.skycore.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goazzi.skycore.R
import com.goazzi.skycore.misc.Enum
import com.goazzi.skycore.misc.Util
import com.goazzi.skycore.viewmodel.MainViewModel

@Composable
fun RestaurantSelectorScreen(
    onSliderPositionChanged: (Float) -> Unit,
    onLocationSwitchChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
//    var sliderPosition by remember { mutableFloatStateOf(100f) }
//    var locationSwitch by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(10.dp)) {
        RadiusSelectorLayout(onSliderPositionChanged)
        LocationSelectorLayout(onLocationSwitchChanged)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = colorResource(id = R.color.purple_700))
        )

        Text(
            text = "Nearby Restaurants:",
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = colorResource(id = R.color.black),
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 10.dp)
        )
    }
}

@Composable
fun RadiusSelectorLayout(onSliderPositionChanged: (Float) -> Unit, modifier: Modifier = Modifier) {
    var sliderPosition by remember { mutableFloatStateOf(100f) }

    Column(
        modifier = modifier.background(color = colorResource(id = R.color.background))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Green)
        ) {
            Text(
                text = stringResource(id = R.string.radius_selector),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = colorResource(id = R.color.black),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "${sliderPosition.toInt()} M",
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = colorResource(id = R.color.black),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Slider(
            value = if (sliderPosition == 0f) 100f else sliderPosition, onValueChange = {
                sliderPosition = if (it == 0f) 100f else it
                onSliderPositionChanged(sliderPosition)
            },
//            value = sliderPosition,
//            onValueChange = { sliderPosition = it },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ), steps = 19, valueRange = 0f..5000f,
//            steps = 4,
//            valueRange = 0f..5f,
            modifier = Modifier.background(color = Color.Yellow)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Green)
        ) {
            Text(
                text = "100 M",
                maxLines = 1,
                color = colorResource(id = R.color.black),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "5 KM",
                maxLines = 1,
                color = colorResource(id = R.color.black),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun LocationSelectorLayout(
    onLocationSwitchChanged: (Boolean) -> Unit, modifier: Modifier = Modifier
) {
    var locationSwitch by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray)
    ) {
        Text(
            text = stringResource(id = R.string.radius_selector),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = colorResource(id = R.color.black),
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Switch(checked = locationSwitch, onCheckedChange = {
            locationSwitch = it
            onLocationSwitchChanged(it)
        }, modifier = Modifier.align(Alignment.CenterVertically))
    }
}

@Composable
@Preview(showBackground = true)
fun RadiusSelectorLayoutPreview() {
    RestaurantSelectorScreen(onSliderPositionChanged = {}, onLocationSwitchChanged = {})
}