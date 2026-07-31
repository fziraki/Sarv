package abkabk.azbarkon.features.tasvirNegar.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    val x: Dp = 0.dp,
    val y: Dp = 0.dp,
)

@Immutable
data class TextLayer(
    val text: String = "",
    val offset: LayerOffset = LayerOffset(),
    val baseFontSizeSp: Float = 16f,
    val color: Color = Color.White,
    val gravity: TextGravity = TextGravity.Center,
    val isBold: Boolean = false,
    val visible: Boolean = true,
)

@Immutable
data class PoetNameLayer(
    val text: String = "",
    val offset: LayerOffset = LayerOffset(y = 120.dp),
    val baseFontSizeSp: Float = 14f,
    val color: Color = Color.White,
    val visible: Boolean = false,
)

@Immutable
data class StickerLayer(
    val assetId: String? = null,
    val galleryUri: String? = null,
    val offset: LayerOffset = LayerOffset(),
    val baseSizeDp: Dp = 48.dp,
    val colorFilter: Color? = null,
    val visible: Boolean = false,
)

@Immutable
data class DividerLayer(
    val assetId: String? = null,
    val offset: LayerOffset = LayerOffset(),
    val baseWidthDp: Dp = 120.dp,
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
    val background: EditorBackground = EditorBackground.SolidColor(TasvirNegarColors.canvasDefault),
    val fontPreset: EditorFontPreset = EditorFontPreset.Yekan,
    val showAlignmentGrid: Boolean = false,
    val selectedLayer: LayerId? = null,
    val sizeProgress: Float = 12f,
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
