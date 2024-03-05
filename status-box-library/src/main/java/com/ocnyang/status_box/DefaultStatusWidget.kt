package com.ocnyang.status_box

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

fun StatusBoxGlobalConfig.initDef() {
    apply {
        errorComponent { DefaultErrorStateView() }
        loadingComponent { DefaultLoadingStateView() }
        emptyComponent { DefaultEmptyStateView() }
        initComponent { DefaultInitialStateView() }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultEmptyStateView(hintText: String = LocalContext.current.getString(R.string.hint_empty), iconRes: Int = R.drawable.ic_empty) {
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(60.dp),
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = "empty",
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(text = hintText, Modifier.padding(10.dp), color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultErrorStateView(hintText: String = LocalContext.current.getString(R.string.hint_error), iconRes: Int = R.drawable.ic_error) {
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(66.dp),
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = "error",
            tint = MaterialTheme.colorScheme.error
        )
        Text(text = hintText, Modifier.padding(10.dp), color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultLoadingStateView() {
    CircularProgressIndicator(modifier = Modifier.size(28.dp), color = MaterialTheme.colorScheme.primary)
}

@Preview(showBackground = true)
@Composable
fun DefaultInitialStateView() {
}
