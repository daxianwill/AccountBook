package com.sym.accountbook.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 可爱粉色系 - 浅色主题
private val CuteLightColorScheme = lightColorScheme(
    primary = PinkPrimary,           // 主色 - 粉色
    onPrimary = WhiteCute,           // 主色上的文字
    primaryContainer = PinkLight,    // 主色容器
    onPrimaryContainer = PinkDark,   // 主色容器上的文字
    
    secondary = PinkDark,            // 次色
    onSecondary = WhiteCute,         // 次色上的文字
    secondaryContainer = PinkLight,  // 次色容器
    onSecondaryContainer = PinkDark, // 次色容器上的文字
    
    tertiary = SoftPurple,           // 第三色
    onTertiary = GrayCute,           // 第三色上的文字
    tertiaryContainer = SoftPurple,  // 第三色容器
    
    background = PinkLight,          // 背景色
    onBackground = GrayCute,         // 背景上的文字
    
    surface = WhiteCute,             // 表面色
    onSurface = GrayCute,            // 表面上的文字
    surfaceVariant = PinkLight,      // 表面变体
    onSurfaceVariant = GrayCute,     // 表面变体上的文字
    
    error = Color(0xFFE57373),       // 错误色
    onError = WhiteCute,             // 错误色上的文字
    
    outline = PinkPrimary,           // 边框色
    outlineVariant = PinkLight,      // 边框变体
)

// 可爱粉色系 - 深色主题（保持粉色调）
private val CuteDarkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    onPrimary = WhiteCute,
    primaryContainer = PinkDark,
    onPrimaryContainer = WhiteCute,
    
    secondary = PinkAccent,
    onSecondary = WhiteCute,
    secondaryContainer = PinkDark,
    onSecondaryContainer = WhiteCute,
    
    tertiary = SoftPurple,
    onTertiary = WhiteCute,
    tertiaryContainer = SoftPurple.copy(alpha = 0.3f),
    
    background = Color(0xFF2D2D2D),
    onBackground = WhiteCute,
    
    surface = Color(0xFF3D3D3D),
    onSurface = WhiteCute,
    surfaceVariant = Color(0xFF4D4D4D),
    onSurfaceVariant = WhiteCute,
    
    error = Color(0xFFEF9A9A),
    onError = WhiteCute,
    
    outline = PinkPrimary,
    outlineVariant = PinkDark,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // 禁用动态颜色，使用我们的粉色主题
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CuteDarkColorScheme
        else -> CuteLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CuteTypography,  // 使用可爱字体风格
        shapes = CuteShapes,         // 使用可爱圆角形状
        content = content
    )
}
