package abkabk.azbarkon.features.tasvir_negar

import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.core.ui_base.LocalAzbarkonAppState
import abkabk.azbarkon.core.ui_base.ObserveAsEvents
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.core.ui_base.asString
import abkabk.azbarkon.features.tasvir_negar.components.EditToolbar
import abkabk.azbarkon.features.tasvir_negar.components.EditorCanvas
import abkabk.azbarkon.features.tasvir_negar.components.EditorFooter
import abkabk.azbarkon.features.tasvir_negar.components.EditorHeader
import abkabk.azbarkon.features.tasvir_negar.components.OptionsRow
import abkabk.azbarkon.features.tasvir_negar.components.VerticalSizeSlider
import abkabk.azbarkon.features.tasvir_negar.util.TasvirCustomColorPicker
import abkabk.azbarkon.features.tasvir_negar.util.rememberTasvirNegarGalleryLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.clear_cancel
import azbarkoncmp.shared.generated.resources.tasvir_help_explanation
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TasvirNegarRoot(
    poemId: Int?,
    onBackClick: () -> Unit,
    viewModel: TasvirNegarViewModel = koinViewModel { parametersOf(poemId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<UiText?>(null) }
    var showColorPicker by remember { mutableStateOf(false) }
    var captureCanvas by remember { mutableStateOf<suspend () -> ByteArray?>({ null }) }
    val scope = rememberCoroutineScope()

    val launchGallery =
        rememberTasvirNegarGalleryLauncher { uri ->
            viewModel.onAction(TasvirNegarAction.OnGalleryImagePicked(uri))
        }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is TasvirNegarEvent.ShowSnackbar -> snackbarMessage = event.message
            TasvirNegarEvent.NavigateBack -> onBackClick()
            TasvirNegarEvent.RequestGalleryPick -> launchGallery()
            TasvirNegarEvent.RequestCustomColorPicker -> showColorPicker = true
            TasvirNegarEvent.RequestExportForSave -> {
                scope.launch {
                    viewModel.onExportCompleted(captureCanvas(), forShare = false)
                }
            }
            TasvirNegarEvent.RequestExportForShare -> {
                scope.launch {
                    viewModel.onExportCompleted(captureCanvas(), forShare = true)
                }
            }
        }
    }

    snackbarMessage?.let { message ->
        val resolvedMessage = message.asString()
        LaunchedEffect(resolvedMessage) {
            appState.showSnackbar(resolvedMessage)
            snackbarMessage = null
        }
    }

    TasvirCustomColorPicker(
        visible = showColorPicker,
        onDismiss = { showColorPicker = false },
        onColorSelected = { color ->
            viewModel.onAction(TasvirNegarAction.OnCustomColorSelected(color))
            showColorPicker = false
        },
    )

    if (state.showHelpDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(TasvirNegarAction.OnDismissHelp) },
            text = { Text(stringResource(Res.string.tasvir_help_explanation)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(TasvirNegarAction.OnDismissHelp) }) {
                    Text(stringResource(Res.string.clear_cancel))
                }
            },
        )
    }

    BaseScreen(
        screenState = state.screenState,
        onRetry = { viewModel.onAction(TasvirNegarAction.OnRetryClick) },
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black),
        ) {
            EditorHeader(
                onSaveClick = { viewModel.onAction(TasvirNegarAction.OnSaveClick) },
                onHelpClick = { viewModel.onAction(TasvirNegarAction.OnShowHelp) },
                onBackClick = onBackClick,
            )

            OptionsRow(
                mode = state.document.activeOptionPanel,
                onColorClick = { viewModel.onAction(TasvirNegarAction.OnColorOptionClick(it)) },
                onShapeClick = { viewModel.onAction(TasvirNegarAction.OnShapeOptionClick(it)) },
                onFontClick = { viewModel.onAction(TasvirNegarAction.OnFontOptionClick(it)) },
            )

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                EditorCanvas(
                    document = state.document,
                    onLayerSelect = { viewModel.onAction(TasvirNegarAction.OnLayerSelect(it)) },
                    onLayerDrag = { layerId, offset ->
                        viewModel.onAction(TasvirNegarAction.OnLayerDrag(layerId, offset))
                    },
                    onPoemTextChange = { viewModel.onAction(TasvirNegarAction.OnPoemTextChange(it)) },
                    onPoetNameChange = { viewModel.onAction(TasvirNegarAction.OnPoetNameChange(it)) },
                    onRemoveSelectedLayer = { viewModel.onAction(TasvirNegarAction.OnRemoveSelectedLayer) },
                    onTextGravityChange = { viewModel.onAction(TasvirNegarAction.OnTextGravityChange(it)) },
                    onToggleTextBold = { viewModel.onAction(TasvirNegarAction.OnToggleTextBold) },
                    onCaptureReady = { captureCanvas = it },
                    modifier = Modifier.fillMaxSize(),
                )

                if (state.document.isEditPanelExpanded) {
                    VerticalSizeSlider(
                        progress = state.document.sizeProgress,
                        onProgressChange = { viewModel.onAction(TasvirNegarAction.OnSizeProgressChange(it)) },
                        modifier =
                            Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 4.dp),
                    )
                }
            }

            if (state.document.isEditPanelExpanded) {
                EditToolbar(onAction = viewModel::onAction)
            }

            EditorFooter(
                onShareClick = { viewModel.onAction(TasvirNegarAction.OnShareClick) },
                onEditClick = { viewModel.onAction(TasvirNegarAction.OnToggleEditPanel) },
                onDeleteClick = { viewModel.onAction(TasvirNegarAction.OnBackClick) },
            )
        }
        }

        if (state.isExporting) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
