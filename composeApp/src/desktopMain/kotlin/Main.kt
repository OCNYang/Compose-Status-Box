import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.ocnyang.demo.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "StatusBox Demo - Desktop"
    ) {
        App()
    }
}
