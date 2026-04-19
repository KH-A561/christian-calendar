package ru.akhilko.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.theme.ColorGreat
import ru.akhilko.core.designsystem.theme.ColorRemembrance

/**
 * Маленький угловой маркер для ячейки месяца.
 * GREAT — золотая точка. REMEMBRANCE — серо-коричневый ромб.
 * FAST/FEAST в Month-сетке обозначаются другим способом (фон/полоски), здесь не рисуются.
 */
@Composable
fun CornerMarker(
    kind: BadgeKind,
    modifier: Modifier = Modifier,
) {
    when (kind) {
        BadgeKind.GREAT -> Canvas(modifier = modifier.size(6.dp)) {
            drawCircle(color = ColorGreat)
        }
        BadgeKind.REMEMBRANCE -> Canvas(modifier = modifier.size(6.dp)) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(w / 2f, 0f)
                lineTo(w, h / 2f)
                lineTo(w / 2f, h)
                lineTo(0f, h / 2f)
                close()
            }
            drawPath(path, color = ColorRemembrance)
        }
        else -> Unit
    }
}
