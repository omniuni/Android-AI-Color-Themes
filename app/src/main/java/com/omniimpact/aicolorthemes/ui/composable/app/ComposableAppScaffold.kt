package com.omniimpact.aicolorthemes.ui.composable.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

import androidx.compose.ui.res.stringResource
import com.omniimpact.aicolorthemes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposableAppScaffold(
	title: String,
	onBackClick: (() -> Unit)? = null,
	actions: @Composable RowScope.() -> Unit = {},
	content: @Composable (PaddingValues) -> Unit
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = title) },
				navigationIcon = {
					if (onBackClick != null) {
						IconButton(onClick = onBackClick) {
							Icon(
								imageVector = Icons.AutoMirrored.Filled.ArrowBack,
								contentDescription = stringResource(R.string.back)
							)
						}
					}
				},
				actions = actions
			)
		},
		contentWindowInsets = WindowInsets.systemBars.union(WindowInsets.ime)
	) { innerPadding ->
		content(innerPadding)
	}
}
