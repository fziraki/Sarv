package abkabk.azbarkon.features.tasvir_negar

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.features.tasvir_negar.model.EditorDocument
import abkabk.azbarkon.features.tasvir_negar.model.LayerId
import abkabk.azbarkon.features.tasvir_negar.model.TextGravity
import abkabk.azbarkon.features.tasvir_negar.model.LayerOffset
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class TasvirNegarState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val document: EditorDocument = EditorDocument(),
    val isExporting: Boolean = false,
    val showHelpDialog: Boolean = false,
)

sealed interface TasvirNegarAction {
    data object OnLoad : TasvirNegarAction

    data object OnRetryClick : TasvirNegarAction

    data object OnBackClick : TasvirNegarAction

    data object OnSaveClick : TasvirNegarAction

    data object OnShareClick : TasvirNegarAction

    data object OnToggleEditPanel : TasvirNegarAction

    data object OnShowColorOptions : TasvirNegarAction

    data object OnShowShapeOptions : TasvirNegarAction

    data object OnShowFontOptions : TasvirNegarAction

    data object OnEnterText : TasvirNegarAction

    data object OnToggleGrid : TasvirNegarAction

    data object OnGalleryClick : TasvirNegarAction

    data object OnShowHelp : TasvirNegarAction

    data object OnDismissHelp : TasvirNegarAction

    data class OnLayerSelect(
        val layerId: LayerId?,
    ) : TasvirNegarAction

    data class OnLayerDrag(
        val layerId: LayerId,
        val offset: LayerOffset,
    ) : TasvirNegarAction

    data class OnSizeProgressChange(
        val progress: Float,
    ) : TasvirNegarAction

    data class OnColorOptionClick(
        val index: Int,
    ) : TasvirNegarAction

    data class OnCustomColorSelected(
        val color: Color,
    ) : TasvirNegarAction

    data class OnShapeOptionClick(
        val index: Int,
    ) : TasvirNegarAction

    data class OnFontOptionClick(
        val index: Int,
    ) : TasvirNegarAction

    data class OnTextGravityChange(
        val gravity: TextGravity,
    ) : TasvirNegarAction

    data object OnToggleTextBold : TasvirNegarAction

    data object OnRemoveSelectedLayer : TasvirNegarAction

    data class OnGalleryImagePicked(
        val uri: String?,
    ) : TasvirNegarAction

    data class OnPoemTextChange(
        val text: String,
    ) : TasvirNegarAction

    data class OnPoetNameChange(
        val text: String,
    ) : TasvirNegarAction
}

sealed interface TasvirNegarEvent {
    data class ShowSnackbar(
        val message: UiText,
    ) : TasvirNegarEvent

    data object NavigateBack : TasvirNegarEvent

    data object RequestGalleryPick : TasvirNegarEvent

    data object RequestExportForSave : TasvirNegarEvent

    data object RequestExportForShare : TasvirNegarEvent

    data object RequestCustomColorPicker : TasvirNegarEvent
}
