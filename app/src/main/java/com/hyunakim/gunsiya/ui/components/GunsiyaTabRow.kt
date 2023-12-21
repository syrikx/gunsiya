package com.hyunakim.gunsiya.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hyunakim.gunsiya.GunsiyaDestination
import java.util.Locale

@Composable
fun GunsiyaTabRow (
    allScreens: List<GunsiyaDestination>,
    onTabSelected: (GunsiyaDestination) -> Unit,
    currentScreen: GunsiyaDestination
) {
//    val isDarkTheme = isSystemInDarkTheme()
//    val backgroundColor = if (isDarkTheme) DarkColors.surface else LightColors.surface
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
//    val backgroundColor = MaterialTheme.colorScheme.surface
    Surface(
        color = backgroundColor,
        modifier = Modifier
            .height(TabHeight)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.background(backgroundColor).selectableGroup()) {
            allScreens.forEach { screen ->
                GunsiyaTab(
                    text = screen.route,
//                    icon = screen.icon,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen
                )
            }
        }
    }
}


@Composable
private fun GunsiyaTab(
    text: String,
//    icon: ImageVector,
    onSelected: () -> Unit,
    selected: Boolean
) {
    val color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
//    val backgroundColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val durationMillis = if (selected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    val animSpec = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = TabFadeInAnimationDelay
        )
    }
    val tabTintColor by animateColorAsState(
        targetValue = if (selected) color else color.copy(alpha = InactiveTabOpacity),
        animationSpec = animSpec
    )
    Row(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize()
            .height(TabHeight)
//            .background(backgroundColor)
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified
                )
            )
            .clearAndSetSemantics { contentDescription = text }
    ) {
//        Icon(imageVector = icon, contentDescription = text, tint = tabTintColor)
        if (selected) {
            Spacer(Modifier.width(12.dp))
            Text(text.uppercase(Locale.getDefault()), color = tabTintColor)
        } else {
            Spacer(Modifier.width(12.dp))
            Text(text.uppercase(Locale.getDefault()), color = tabTintColor)
        }
    }
}
//@Composable
//private fun GunsiyaTab(
//    text: String,
//    onSelected: () -> Unit,
//    selected: Boolean
//) {
//    // 선택된 탭의 배경색과 텍스트 색상을 결정합니다.
//    val backgroundColor = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
//    val textColor = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary
//
//    Row(
//        modifier = Modifier
//            .padding(16.dp)
//            .height(TabHeight)
//            .background(backgroundColor) // 배경색 적용
//            .selectable(
//                selected = selected,
//                onClick = onSelected,
//                role = Role.Tab,
//                interactionSource = remember { MutableInteractionSource() },
//                indication = rememberRipple(
//                    bounded = false,
//                    radius = Dp.Unspecified,
//                    color = Color.Unspecified
//                )
//            )
//            .clearAndSetSemantics { contentDescription = text }
//    ) {
//        Spacer(Modifier.width(12.dp))
//        Text(
//            text = text.uppercase(Locale.getDefault()),
//            color = textColor // 텍스트 색상 적용
//        )
//    }
//}


private val TabHeight = 56.dp
private const val InactiveTabOpacity = 0.60f

private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100