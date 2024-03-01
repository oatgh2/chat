package app.oatgh.listadetarefascompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFF5AB9EA), // Azul Claro
    primaryVariant = Color(0xFF4A4A4A), // Cinza Escuro
    secondary = Color(0xFFFFA500), // Laranja
    background = Color(0xFF121212), // Cinza Escuro
    surface = Color(0xFF121212), // Cinza Escuro
    error = Color(0xFFB00020), // Vermelho
    onPrimary = Color(0xFFFFFFFF), // Branco
    onSecondary = Color(0xFFFFFFFF), // Branco
    onBackground = Color(0xFFFFFFFF), // Branco
    onSurface = Color(0xFFFFFFFF), // Branco
    onError = Color(0xFFFFFFFF) // Branco
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF5AB9EA), // Azul Claro
    primaryVariant = Color(0xFF4A4A4A), // Cinza Escuro
    secondary = Color(0xFFFFA500), // Laranja
    secondaryVariant = Color(0xFF7FFFD4), // Verde-água
    background = Color(0xFFFFFFFF), // Branco
    surface = Color(0xFFFFFFFF), // Branco
    error = Color(0xFFB00020), // Vermelho
    onPrimary = Color(0xFF000000), // Preto
    onSecondary = Color(0xFF000000), // Preto
    onBackground = Color(0xFF000000), // Preto
    onSurface = Color(0xFF000000), // Preto
    onError = Color(0xFFFFFFFF) // Branco
)

@Composable
fun ListaDeTarefasComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content

    )
}