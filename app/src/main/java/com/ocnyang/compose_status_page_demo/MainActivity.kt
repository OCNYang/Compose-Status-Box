package com.ocnyang.compose_status_page_demo

import android.content.Intent
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ocnyang.compose_loading.InstaSpinner
import com.ocnyang.compose_status_page_demo.ui.theme.ComposeStatusPageTheme
import com.ocnyang.status_box.StatusBox
import com.ocnyang.status_box.UIState
import kotlinx.serialization.Serializable

@Serializable
object MainRoute

@Serializable
object NextRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeStatusPageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = MainRoute,
                    ) {
                        composable<MainRoute> {
                            ContentBox(
                                modifier = Modifier.fillMaxSize(),
                                nav2Next = {
                                    navController.navigate(NextRoute)
                                })
                        }
                        composable<NextRoute> {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ContentBox(
    modifier: Modifier = Modifier,
    nav2Next: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val uiStateState = viewModel.uiState.collectAsStateWithLifecycle()
    val loadingState = viewModel.loadingState.collectAsStateWithLifecycle()
    val contentCount = viewModel.itemNumber.collectAsStateWithLifecycle()

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .shadow(elevation = 1.dp)
                    .padding(vertical = 10.dp)
            ) {
                Button(
                    onClick = { viewModel.changeState(MainUIState.Success(2, "Success")) },
                    content = { Text(text = "Success") })
                Button(
                    onClick = { viewModel.changeState(UIState.Error("Error")) },
                    content = { Text(text = "Error") })
                Button(
                    onClick = { viewModel.changeState(UIState.Empty()) },
                    content = { Text(text = "No Data") })
                Button(
                    onClick = { viewModel.changeLoading(true) },
                    content = { Text(text = "Loading") })
                Button(onClick = {
                    viewModel.apply {
                        changeState(UIState.Initial)
                        changeLoading(visible = true)
                    }
                }, content = { Text(text = "Reload") })
            }
        },
        floatingActionButton = {
            Column {
                if (uiStateState.value is MainUIState.Success) {
                    SmallFloatingActionButton(
                        onClick = {
                            viewModel.changeItemNumber(contentCount.value + 1)
                        },
                        content = {
                            Icon(Icons.Filled.Add, contentDescription = "add-child-view btn")
                        },
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    SmallFloatingActionButton(
                        onClick = {
                            viewModel.changeItemNumber(contentCount.value - 1)
                        },
                        content = {
                            Icon(Icons.Filled.Clear, contentDescription = "remove-child-view btn")
                        },
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    val content = LocalContext.current
                    SmallFloatingActionButton(
                        onClick = {
                            content.startActivity(Intent(content, MainActivity::class.java))
                        },
                        content = {
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = "remove-child-view btn"
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    SmallFloatingActionButton(
                        onClick = {
                            nav2Next()
                        },
                        content = {
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = "remove-child-view btn"
                            )
                        },
                    )
                }
            }
        }
    ) { paddingValues ->
        StatusBox<MainUIState.Success>(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            uiState = uiStateState.value,
            loadingState = loadingState.value,
            loadingComponentBlock = { InstaSpinner(size = 25.dp) } // The default is global configuration, which you can set for each page individually
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                for (i in 0..contentCount.value) {
                    ContentItemBox(text = "Child $i: ${it.data}")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentItemBox(text: String = "TEXT") =
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.White)
    ) {
        Text(modifier = Modifier.align(Alignment.Center), text = text)
        HorizontalDivider(color = Color.Gray, modifier = Modifier.align(Alignment.BottomCenter))
    }

