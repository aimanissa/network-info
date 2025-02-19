package com.aimanissa.features.connection.ui.views

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aimanissa.base.theme.ui.NetInfoTheme
import com.aimanissa.features.connection.ui.ViewState

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ConnectionScreen(
    modifier: Modifier = Modifier,
    uiState: ViewState
) {
    var cardWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .onGloballyPositioned {
                    cardWidth = with(density) {
                        it.size.width.toDp()
                    }
                }
            ) {
                Row {
                    LazyColumn(
                        modifier = Modifier.width(cardWidth / 2),
                        state = rememberLazyListState()
                    ) {
                        items(uiState.titles) { title ->
                            Text(text = stringResource(title))
                        }
                    }

                    Column(
                        modifier = Modifier.width(cardWidth / 2)
                    ) {
                        Text(text = "192/286 Mbps")
                        Text(text = "Up to 154 Mbps/sec")
                        Text(text = "2422 Mhz")
                        Text(text = "3")
                        Text(text = "192.168.100.1")
                        Text(text = "192.168.100.1")
                        Text(text = "192.168.100.1")
                        Text(text = "192.168.100.1")
                        Text(text = "192.168.100.1")
                        Text(text = "192.168.100.1")
                        Text(text = "192.168.100.1")
                    }
                }


            }
        }

    }
}

@Preview(
    showBackground = true,
    name = "Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun ConnectionScreenPreview() {
    NetInfoTheme {
        ConnectionScreen(uiState = ViewState())
    }
}
