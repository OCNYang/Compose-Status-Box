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
import com.ocnyang.status_box.resources.hint_empty
import com.ocnyang.status_box.resources.hint_error
import com.ocnyang.status_box.resources.ic_empty
import com.ocnyang.status_box.resources.ic_error
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
    IconTextMapContent(
        hintText = hintText ?: stringResource(Res.string.hint_empty),
        iconPainter = iconPainter ?: painterResource(Res.drawable.ic_empty),
        iconTint = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun DefaultErrorStateView(
    hintText: String? = null,
    iconPainter: Painter? = null
) {
    IconTextMapContent(
        hintText = hintText ?: stringResource(Res.string.hint_error),
        iconPainter = iconPainter ?: painterResource(Res.drawable.ic_error),
        iconTint = MaterialTheme.colorScheme.error
    )
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

@Composable
private fun IconTextMapContent(
    hintText: String,
    iconPainter: Painter,
    iconTint: Color,
) {
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(45.dp),
            painter = iconPainter,
            contentDescription = "error",
            tint = iconTint
        )
        Text(
            text = hintText,
            modifier = Modifier.padding(10.dp),
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}