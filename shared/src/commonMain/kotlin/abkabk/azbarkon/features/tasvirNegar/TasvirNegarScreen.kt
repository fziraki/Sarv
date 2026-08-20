package abkabk.azbarkon.features.tasvirNegar

import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.features.tasvirNegar.components.EditToolbar
import abkabk.azbarkon.features.tasvirNegar.components.EditorCallbacks
import abkabk.azbarkon.features.tasvirNegar.components.EditorCanvas
import abkabk.azbarkon.features.tasvirNegar.components.EditorFooter
import abkabk.azbarkon.features.tasvirNegar.components.EditorHeader
import abkabk.azbarkon.features.tasvirNegar.components.OptionsRow
import abkabk.azbarkon.features.tasvirNegar.components.VerticalSizeSlider
import abkabk.azbarkon.features.tasvirNegar.model.EditorDocument
import abkabk.azbarkon.features.tasvirNegar.model.LayerId
import abkabk.azbarkon.features.tasvirNegar.util.TasvirCustomColorPicker
import abkabk.azbarkon.features.tasvirNegar.util.rememberTasvirNegarGalleryLauncher
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TasvirNegarRoot(
    poemId: Int?,
    initialText: String?,
    onBackClick: () -> Unit,
    viewModel: TasvirNegarViewModel = koinViewModel { parametersOf(poemId, initialText) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showColorPicker by remember { mutableStateOf(false) }
    var captureCanvas by remember { mutableStateOf<suspend () -> ByteArray?>({ null }) }
    val captureCanvasState by rememberUpdatedState(captureCanvas)

    val launchGallery =
        rememberTasvirNegarGalleryLauncher { uri ->
            viewModel.onAction(TasvirNegarAction.OnGalleryImagePicked(uri))
        }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
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

    TasvirCustomColorPicker(
        visible = showColorPicker,
        onDismiss = { showColorPicker = false },
        onColorSelect = { color ->
            viewModel.onAction(TasvirNegarAction.OnCustomColorSelected(color))
            showColorPicker = false
        },
    )

    BaseScreen(
        screenState = state.screenState,
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
    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                EditorHeader(
                    onResetClick = { onAction(TasvirNegarAction.OnResetCanvas) },
                    onBackClick = onBackClick,
                )
                if (!state.isExporting) {
                    OptionsRow(
                        mode = state.document.activeOptionPanel,
                        onColorClick = { onAction(TasvirNegarAction.OnColorOptionClick(it)) },
                        onShapeClick = { onAction(TasvirNegarAction.OnShapeOptionClick(it)) },
                        onFontClick = { onAction(TasvirNegarAction.OnFontOptionClick(it)) },
                    )
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!state.isExporting && state.document.isEditPanelExpanded) {
                    EditToolbar(onAction = onAction)
                }

                EditorFooter(
                    onEraserClick = { onAction(TasvirNegarAction.OnEraserClick) },
                    onDownloadClick = { onAction(TasvirNegarAction.OnSaveClick) },
                    onEditClick = { onAction(TasvirNegarAction.OnToggleEditPanel) },
                    onShareClick = { onAction(TasvirNegarAction.OnShareClick) },
                )
            }
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(vertical = 128.dp),
            contentAlignment = Alignment.Center,
        ) {
            EditorCanvas(
                document = state.document,
                callbacks =
                    EditorCallbacks(
                        onLayerSelect = { onAction(TasvirNegarAction.OnLayerSelect(it)) },
                        onLayerDrag = { layerId, offset ->
                            onAction(TasvirNegarAction.OnLayerDrag(layerId, offset))
                        },
                        onPoemTextChange = { onAction(TasvirNegarAction.OnPoemTextChange(it)) },
                        onPoetNameChange = { onAction(TasvirNegarAction.OnPoetNameChange(it)) },
                        onTextGravityChange = { onAction(TasvirNegarAction.OnTextGravityChange(it)) },
                        onToggleTextBold = { onAction(TasvirNegarAction.OnToggleTextBold) },
                        onCaptureReady = onCaptureReady,
                    ),
                showEditOverlays = !state.isExporting,
                modifier = Modifier.fillMaxSize(),
            )

            if (!state.isExporting &&
                state.document.isEditPanelExpanded &&
                state.document.selectedLayer != null
            ) {
                VerticalSizeSlider(
                    progress = sizeProgressFor(state.document),
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

private fun sizeProgressFor(document: EditorDocument): Float =
    when (document.selectedLayer) {
        LayerId.PoemText -> document.poemText.sizeProgress
        LayerId.PoetName -> document.poetName.sizeProgress
        LayerId.Sticker -> document.sticker.sizeProgress
        LayerId.TopDivider -> document.topDivider.sizeProgress
        LayerId.BottomDivider -> document.bottomDivider.sizeProgress
        null -> 0f
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
