package abkabk.azbarkon.features.tasvirNegar.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import abkabk.azbarkon.core.designsystem.SarvDimensions

enum class LayerId {
    PoemText,
    PoetName,
    Sticker,
    TopDivider,
    BottomDivider,
}

enum class TextGravity {
    Start,
    Center,
    End,
}

enum class EditorFontPreset {
    Shekasteh,
    Yekan,
    Tanha,
}

enum class OptionPanelMode {
    None,
    Color,
    Shape,
    Font,
}

@Immutable
data class LayerOffset(
    val x: Dp = SarvDimensions.dimen0,
    val y: Dp = SarvDimensions.dimen0,
)

@Immutable
data class TextLayer(
    val text: String = "",
    val offset: LayerOffset = LayerOffset(),
    val baseFontSizeSp: Float = 16f,
    val sizeProgress: Float = 1f,
    val color: Color = Color.White,
    val gravity: TextGravity = TextGravity.Center,
    val isBold: Boolean = false,
    val visible: Boolean = true,
)

@Immutable
data class PoetNameLayer(
    val text: String = "",
    val offset: LayerOffset = LayerOffset(y = SarvDimensions.dimen120),
    val baseFontSizeSp: Float = 14f,
    val sizeProgress: Float = 1f,
    val color: Color = Color.White,
    val visible: Boolean = false,
)

@Immutable
data class StickerLayer(
    val assetId: String? = null,
    val galleryUri: String? = null,
    val offset: LayerOffset = LayerOffset(),
    val baseSizeDp: Dp = SarvDimensions.dimen48,
    val sizeProgress: Float = 1f,
    val colorFilter: Color? = null,
    val visible: Boolean = false,
)

@Immutable
data class DividerLayer(
    val assetId: String? = null,
    val offset: LayerOffset = LayerOffset(),
    val baseWidthDp: Dp = SarvDimensions.dimen120,
    val sizeProgress: Float = 1f,
    val colorFilter: Color? = null,
    val visible: Boolean = false,
)

sealed interface EditorBackground {
    data object None : EditorBackground

    data class SolidColor(
        val color: Color,
    ) : EditorBackground

    data class GalleryImage(
        val uri: String,
    ) : EditorBackground

    data class CatalogTexture(
        val drawableName: String,
    ) : EditorBackground
}

@Immutable
data class EditorDocument(
    val poemText: TextLayer = TextLayer(),
    val poetName: PoetNameLayer = PoetNameLayer(),
    val sticker: StickerLayer = StickerLayer(),
    val topDivider: DividerLayer = DividerLayer(),
    val bottomDivider: DividerLayer = DividerLayer(),
    val background: EditorBackground = EditorBackground.SolidColor(Color.Transparent),
    val fontPreset: EditorFontPreset = EditorFontPreset.Shekasteh,
    val showAlignmentGrid: Boolean = false,
    val selectedLayer: LayerId? = null,
    val activeOptionPanel: OptionPanelMode = OptionPanelMode.None,
    val isEditPanelExpanded: Boolean = false,
)

@Immutable
data class CatalogItem(
    val id: String,
    val drawableName: String,
)

@Immutable
data class ColorOption(
    val color: Color? = null,
    val isCustomPicker: Boolean = false,
)
