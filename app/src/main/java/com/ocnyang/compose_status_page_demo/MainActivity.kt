package com.ocnyang.compose_status_page_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ocnyang.compose_loading.InstaSpinner
import com.ocnyang.compose_status_page_demo.ui.theme.ComposeStatusPageTheme
import com.ocnyang.status_box.StateContainer
import com.ocnyang.status_box.StatusBox
import com.ocnyang.status_box.UIState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeStatusPageTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ContentBox()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ContentBox(modifier: Modifier = Modifier) {
    val stateContainer = StateContainer<String>(state = UIState.Initial, loadingState = true to "")
    val uiStateState = stateContainer.uiStateFlow.collectAsState()
    val contentCount = remember { mutableIntStateOf(1) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(text = LocalContext.current.getString(R.string.app_name))
                })
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).shadow(elevation = 1.dp).padding(vertical = 10.dp)) {
                Button(onClick = { stateContainer.changeState(UIState.Success("Success")) }, content = { Text(text = "Success") })
                Button(onClick = { stateContainer.changeState(UIState.Error("Error")) }, content = { Text(text = "Error") })
                Button(onClick = { stateContainer.changeState(UIState.Empty()) }, content = { Text(text = "No Data") })
                Button(onClick = { stateContainer.changeLoading(true) }, content = { Text(text = "Loading") })
                Button(onClick = {
                    stateContainer.apply {
                        changeState(UIState.Initial)
                        changeLoading(visible = true)
                    }
                }, content = { Text(text = "Reload") })
            }
        },
        floatingActionButton = {
            Column {
                if (uiStateState.value is UIState.Success) {
                    SmallFloatingActionButton(
                        onClick = {
                            contentCount.value = contentCount.value + 1
                        },
                        content = {
                            Icon(Icons.Filled.Add, contentDescription = "add-child-view btn")
                        },
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    SmallFloatingActionButton(
                        onClick = {
                            contentCount.value = contentCount.value - 1
                        },
                        content = {
                            Icon(Icons.Filled.Clear, contentDescription = "remove-child-view btn")
                        },
                    )
                }
            }
        }
    ) { paddingValues ->
        StatusBox(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            stateContainer = stateContainer,
            contentScrollEnabled = true,
            loadingComponentBlock = { InstaSpinner(size = 25.dp) } // The default is global configuration, which you can set for each page individually
        ) {
            for (i in 0..contentCount.value) {
                ContentItemBox(text = "Child $i: ${it.data}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentItemBox(text: String = "TEXT") = Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(Color.White)) {
    Text(modifier = Modifier.align(Alignment.Center), text = text)
    HorizontalDivider(color = Color.Gray, modifier = Modifier.align(Alignment.BottomCenter))
}

