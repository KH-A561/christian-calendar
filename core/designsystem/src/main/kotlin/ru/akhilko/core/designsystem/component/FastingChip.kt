package ru.akhilko.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.akhilko.core.designsystem.theme.ColorFast

@Composable
fun FastingChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(100),
        color = ColorFast.copy(alpha = 0.1f),
        contentColor = ColorFast,
        border = BorderStroke(1.dp, ColorFast.copy(alpha = 0.4f)),
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(PaddingValues(horizontal = 12.dp, vertical = 6.dp)),
        )
    }
}
