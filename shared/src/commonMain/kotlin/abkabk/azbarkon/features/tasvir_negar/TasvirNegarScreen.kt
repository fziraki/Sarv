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
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.clear_cancel
import azbarkoncmp.shared.generated.resources.tasvir_help_explanation
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
    val captureCanvasState by rememberUpdatedState(captureCanvas)

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
        }
    }

    LaunchedEffect(state.isExporting) {
        if (!state.isExporting) return@LaunchedEffect
        val forShare = state.exportForShare
        withFrameNanos { }
        withFrameNanos { }
        viewModel.onExportCompleted(captureCanvasState(), forShare = forShare)
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

    BaseScreen(
        screenState = state.screenState,
        onRetry = { viewModel.onAction(TasvirNegarAction.OnRetryClick) },
    ) {
        TasvirNegarScreen(
            state = state,
            onAction = viewModel::onAction,
            onBackClick = onBackClick,
            onCaptureReady = { captureCanvas = it },
        )
    }
}

@Composable
fun TasvirNegarScreen(
    state: TasvirNegarState,
    onAction: (TasvirNegarAction) -> Unit,
    onBackClick: () -> Unit,
    onCaptureReady: (suspend () -> ByteArray?) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.showHelpDialog) {
        AlertDialog(
            onDismissRequest = { onAction(TasvirNegarAction.OnDismissHelp) },
            text = { Text(stringResource(Res.string.tasvir_help_explanation)) },
            confirmButton = {
                TextButton(onClick = { onAction(TasvirNegarAction.OnDismissHelp) }) {
                    Text(stringResource(Res.string.clear_cancel))
                }
            },
        )
    }

        Scaffold(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            topBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    EditorHeader(
                        onSaveClick = { onAction(TasvirNegarAction.OnSaveClick) },
                        onHelpClick = { onAction(TasvirNegarAction.OnShowHelp) },
                        onBackClick = onBackClick,
                    )
                    OptionsRow(
                        mode = state.document.activeOptionPanel,
                        onColorClick = { onAction(TasvirNegarAction.OnColorOptionClick(it)) },
                        onShapeClick = { onAction(TasvirNegarAction.OnShapeOptionClick(it)) },
                        onFontClick = { onAction(TasvirNegarAction.OnFontOptionClick(it)) },
                    )
                }
            },
            bottomBar = {

                Column(modifier = Modifier.fillMaxWidth()) {
                    if (!state.isExporting && state.document.isEditPanelExpanded) {
                        EditToolbar(onAction = onAction)
                    }

                    EditorFooter(
                        onShareClick = { onAction(TasvirNegarAction.OnShareClick) },
                        onEditClick = { onAction(TasvirNegarAction.OnToggleEditPanel) },
                        onDeleteClick = { onAction(TasvirNegarAction.OnResetCanvas) },
                    )
                }
            }
        ) {

            Box(
                modifier = Modifier.fillMaxSize().padding(vertical = 128.dp),
                contentAlignment = Alignment.Center,
            ) {
                EditorCanvas(
                    document = state.document,
                    onLayerSelect = { onAction(TasvirNegarAction.OnLayerSelect(it)) },
                    onLayerDrag = { layerId, offset ->
                        onAction(TasvirNegarAction.OnLayerDrag(layerId, offset))
                    },
                    onPoemTextChange = { onAction(TasvirNegarAction.OnPoemTextChange(it)) },
                    onPoetNameChange = { onAction(TasvirNegarAction.OnPoetNameChange(it)) },
                    onRemoveSelectedLayer = { onAction(TasvirNegarAction.OnRemoveSelectedLayer) },
                    onTextGravityChange = { onAction(TasvirNegarAction.OnTextGravityChange(it)) },
                    onToggleTextBold = { onAction(TasvirNegarAction.OnToggleTextBold) },
                    onCaptureReady = onCaptureReady,
                    showEditChrome = !state.isExporting,
                    modifier = Modifier.fillMaxSize(),
                )

                if (!state.isExporting && state.document.isEditPanelExpanded) {
                    VerticalSizeSlider(
                        progress = state.document.sizeProgress,
                        onProgressChange = { onAction(TasvirNegarAction.OnSizeProgressChange(it)) },
                        modifier =
                            Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 4.dp),
                    )
                }

            }

        }

}

@Preview
@Composable
private fun TasvirNegarScreenPreview() {
    AzbarkonTheme {
        TasvirNegarScreen(
            state = TasvirNegarState(),
            onAction = {},
            onBackClick = {},
            onCaptureReady = {},
        )
    }
}
