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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ocnyang.status_box.resources.Res
import com.ocnyang.status_box.resources.ic_empty
import com.ocnyang.status_box.resources.ic_error
import com.ocnyang.status_box.resources.hint_empty
import com.ocnyang.status_box.resources.hint_error
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun StatusBoxGlobalConfig.initDef() {
    apply {
        errorComponent { DefaultErrorStateView() }
        loadingComponent { DefaultLoadingStateView() }
        emptyComponent { DefaultEmptyStateView() }
        initComponent { DefaultInitialStateView() }
    }
}

@Composable
fun DefaultEmptyStateView(
    hintText: String? = null,
    iconPainter: Painter? = null
) {
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(25.dp),
            painter = iconPainter ?: painterResource(Res.drawable.ic_empty),
            contentDescription = "empty",
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = hintText ?: stringResource(Res.string.hint_empty),
            modifier = Modifier.padding(10.dp),
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun DefaultErrorStateView(
    hintText: String? = null,
    iconPainter: Painter? = null
) {
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(25.dp),
            painter = iconPainter ?: painterResource(Res.drawable.ic_error),
            contentDescription = "error",
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = hintText ?: stringResource(Res.string.hint_error),
            modifier = Modifier.padding(10.dp),
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun DefaultLoadingStateView() {
    CircularProgressIndicator(
        modifier = Modifier.size(25.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun DefaultInitialStateView() {
    // Empty implementation
}
