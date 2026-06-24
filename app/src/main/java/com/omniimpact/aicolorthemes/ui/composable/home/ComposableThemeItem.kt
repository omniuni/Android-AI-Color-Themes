package com.omniimpact.aicolorthemes.ui.composable.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.omniimpact.aicolorthemes.model.ModelColorTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComposableThemeItem(theme: ModelColorTheme, onRemove: () -> Unit) {
	val context = LocalContext.current
	val clipboardManager = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
	Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
				Column(modifier = Modifier.weight(1f)) {
					Text(text = theme.themeName, style = MaterialTheme.typography.titleMedium)
				}
				IconButton(onClick = onRemove, modifier = Modifier.padding(start = 8.dp)) {
					Icon(imageVector = Icons.Default.Close, contentDescription = "Remove")
				}
			}
			Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
				Text(text = theme.themeDescription, style = MaterialTheme.typography.bodyMedium)
			}
			FlowRow(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
				theme.colorTheme.forEach { colorHex ->
					val colorInt = colorHex.toColorInt()
					Box(
						modifier = Modifier
							.size(90.dp, 40.dp)
							.clip(RoundedCornerShape(4.dp))
							.background(Color(colorInt))
							.clickable {
								val clip = ClipData.newPlainText("Color", colorHex)
								clipboardManager.setPrimaryClip(clip)
							},
						contentAlignment = Alignment.Center
					) {
						Text(
							text = colorHex,
							style = MaterialTheme.typography.labelSmall,
							fontFamily = FontFamily.Monospace,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
							color = if (Color(colorInt).luminance() > 0.5f) Color.Black else Color.White
						)
					}
				}
			}
		}
	}
}
