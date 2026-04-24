package ru.akhilko.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.theme.ColorEaster
import ru.akhilko.core.designsystem.theme.ColorGreatFeast
import ru.akhilko.core.designsystem.theme.ColorFast
import ru.akhilko.core.designsystem.theme.ColorFeast
import ru.akhilko.core.designsystem.theme.ColorRemembrance
import ru.akhilko.core.designsystem.theme.ColorTwelveFeast

enum class BadgeKind(val label: String, val color: Color, val onColor: Color = Color.White) {
    EASTER("Пасха", ColorEaster),
    TWELVE("Двунадесятый", ColorTwelveFeast),
    GREAT("Великий праздник", ColorGreatFeast, onColor = Color(0xFF5C0000)),
    FEAST("Праздник", ColorFeast),
    REMEMBRANCE("Память", ColorRemembrance),
    FAST("Пост", ColorFast),
}

@Composable
fun DayTypeBadge(
    kind: BadgeKind,
    modifier: Modifier = Modifier,
) {
    Text(
        text = kind.label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = kind.onColor,
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(kind.color)
            .padding(PaddingValues(horizontal = 10.dp, vertical = 4.dp)),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DayTypeBadgeRow(
    kinds: List<BadgeKind>,
    modifier: Modifier = Modifier,
) {
    if (kinds.isEmpty()) return
    FlowRow(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
    ) {
        kinds.forEach { DayTypeBadge(it) }
    }
}
