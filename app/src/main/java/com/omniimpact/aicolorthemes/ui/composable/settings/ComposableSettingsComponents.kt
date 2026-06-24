package com.omniimpact.aicolorthemes.ui.composable.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

interface IComposableTextSetting {
	val name: String
	val placeholder: String
	val key: String
	val value: String
	val onValueChange: (String) -> Unit
}

@Composable
fun ComposableTextSetting(
	setting: IComposableTextSetting,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
		Text(text = setting.name)
		OutlinedTextField(
			value = setting.value,
			onValueChange = setting.onValueChange,
			placeholder = { Text(text = setting.placeholder) },
			modifier = Modifier.fillMaxWidth()
		)
	}
}

interface IComposableDropdownSetting {
	val name: String
	val selectedOption: String
	val options: List<String>
	val onOptionSelected: (String) -> Unit
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableDropdownSetting(
	setting: IComposableDropdownSetting,
	modifier: Modifier = Modifier
) {
	var expanded by remember { mutableStateOf(false) }

	Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
		Text(text = setting.name)
		ExposedDropdownMenuBox(
			expanded = expanded,
			onExpandedChange = { expanded = !expanded },
			modifier = Modifier.fillMaxWidth()
		) {
			OutlinedTextField(
				value = setting.selectedOption,
				onValueChange = {},
				readOnly = true,
				trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
				colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
				modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).fillMaxWidth()
			)
			ExposedDropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false }
			) {
				setting.options.forEach { option ->
					DropdownMenuItem(
						text = { Text(text = option) },
						onClick = {
							setting.onOptionSelected(option)
							expanded = false
						}
					)
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun PreviewComposableTextSetting() {
	val mockSetting = object : IComposableTextSetting {
		override val name = "API Key"
		override val placeholder = "Enter your API key"
		override val key = "api_key"
		override val value = "mock_api_key_123"
		override val onValueChange = { _: String -> }
	}
	ComposableTextSetting(setting = mockSetting)
}

@Preview(showBackground = true)
@Composable
fun PreviewComposableDropdownSetting() {
	val mockSetting = object : IComposableDropdownSetting {
		override val name = "Theme"
		override val selectedOption = "Dark"
		override val options = listOf("Light", "Dark")
		override val onOptionSelected = { _: String -> }
	}
	ComposableDropdownSetting(setting = mockSetting)
}
