package abkabk.azbarkon.features.tasvir_negar.components

import abkabk.azbarkon.core.designsystem.secondary
import abkabk.azbarkon.features.tasvir_negar.model.EditorBackground
import abkabk.azbarkon.features.tasvir_negar.model.EditorDocument
import abkabk.azbarkon.features.tasvir_negar.model.LayerId
import abkabk.azbarkon.features.tasvir_negar.model.TextGravity
import abkabk.azbarkon.features.tasvir_negar.util.LocalGalleryImage
import abkabk.azbarkon.features.tasvir_negar.util.rememberCanvasCaptureModifier
import abkabk.azbarkon.features.tasvir_negar.util.tasvirNegarPainter
import abkabk.azbarkon.features.tasvir_negar.util.textAlignForGravity
import abkabk.azbarkon.features.tasvir_negar.util.textDirectionFor
import abkabk.azbarkon.features.tasvir_negar.util.textLayoutDirectionFor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.ic_align_center
import azbarkoncmp.shared.generated.resources.ic_align_left
import azbarkoncmp.shared.generated.resources.ic_align_right
import azbarkoncmp.shared.generated.resources.ic_bold
import org.jetbrains.compose.resources.painterResource

@Composable
fun EditorCanvas(
    document: EditorDocument,
    onLayerSelect: (LayerId?) -> Unit,
    onLayerDrag: (LayerId, abkabk.azbarkon.features.tasvir_negar.model.LayerOffset) -> Unit,
    onPoemTextChange: (String) -> Unit,
    onPoetNameChange: (String) -> Unit,
    onTextGravityChange: (TextGravity) -> Unit,
    onToggleTextBold: () -> Unit,
    onCaptureReady: (suspend () -> ByteArray?) -> Unit,
    showEditOverlays: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val captureModifier = rememberCanvasCaptureModifier(onCaptureReady = onCaptureReady)
    val fontFamily = editorFontFamily(document.fontPreset)

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val canvasSize = minOf(maxWidth, maxHeight)
        val minLayerWidth = canvasSize * 0.2f
        val maxLayerWidth = canvasSize * 0.95f
        val isPoemEditing =
            showEditOverlays &&
                document.isEditPanelExpanded &&
                document.selectedLayer == LayerId.PoemText
        val isPoetEditing =
            showEditOverlays &&
                document.isEditPanelExpanded &&
                document.selectedLayer == LayerId.PoetName

        Box(
            modifier =
                Modifier
                    .size(canvasSize)
                    .then(captureModifier)
                    .clipToBounds(),
        ) {
            // Canvas uses physical LTR coordinates so pan/drag matches finger movement on RTL screens.
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                CanvasBackground(
                    document = document,
                    onBackgroundTap = {
                        if (showEditOverlays) {
                            onLayerSelect(null)
                        }
                    },
                )

                if (document.showAlignmentGrid && showEditOverlays) {
                    AlignmentGrid(modifier = Modifier.fillMaxSize())
                }

                if (document.poemText.visible) {
                    DraggableLayer(
                        offset = document.poemText.offset,
                        selected = document.selectedLayer == LayerId.PoemText,
                        showLayerControls = showEditOverlays,
                        onSelect = { onLayerSelect(LayerId.PoemText) },
                        onDrag = { onLayerDrag(LayerId.PoemText, it) },
                        showTextControls = showEditOverlays && document.selectedLayer == LayerId.PoemText,
                        isBold = document.poemText.isBold,
                        onTextGravityChange = onTextGravityChange,
                        onToggleBold = onToggleTextBold,
                        minWidth = minLayerWidth,
                    ) {
                        DirectionalTextField(
                            value = document.poemText.text,
                            onValueChange = onPoemTextChange,
                            readOnly = !isPoemEditing,
                            textStyle =
                                TextStyle(
                                    color = document.poemText.color,
                                    fontSize = (document.poemText.baseFontSizeSp + document.sizeProgress).sp,
                                    fontWeight =
                                        if (document.poemText.isBold) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                    textAlign = textAlignForGravity(document.poemText.gravity),
                                    fontFamily = fontFamily,
                                ),
                            modifier =
                                Modifier
                                    .widthIn(min = minLayerWidth, max = maxLayerWidth)
                                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                        )
                    }
                }

                if (document.topDivider.visible && document.topDivider.assetId != null) {
                    DraggableLayer(
                        offset = document.topDivider.offset,
                        selected = document.selectedLayer == LayerId.TopDivider,
                        showLayerControls = showEditOverlays,
                        onSelect = { onLayerSelect(LayerId.TopDivider) },
                        onDrag = { onLayerDrag(LayerId.TopDivider, it) },
                        alignment = Alignment.TopCenter,
                        minWidth = minLayerWidth,
                    ) {
                        DividerImage(
                            assetId = document.topDivider.assetId,
                            width = document.topDivider.baseWidthDp + (document.sizeProgress * 4).dp,
                            colorFilter = document.topDivider.colorFilter,
                            flipVertical = false,
                        )
                    }
                }

                if (document.bottomDivider.visible && document.bottomDivider.assetId != null) {
                    DraggableLayer(
                        offset = document.bottomDivider.offset,
                        selected = document.selectedLayer == LayerId.BottomDivider,
                        showLayerControls = showEditOverlays,
                        onSelect = { onLayerSelect(LayerId.BottomDivider) },
                        onDrag = { onLayerDrag(LayerId.BottomDivider, it) },
                        alignment = Alignment.BottomCenter,
                        minWidth = minLayerWidth,
                    ) {
                        DividerImage(
                            assetId = document.bottomDivider.assetId,
                            width = document.bottomDivider.baseWidthDp + (document.sizeProgress * 4).dp,
                            colorFilter = document.bottomDivider.colorFilter,
                            flipVertical = true,
                        )
                    }
                }

                if (document.poetName.visible) {
                    DraggableLayer(
                        offset = document.poetName.offset,
                        selected = document.selectedLayer == LayerId.PoetName,
                        showLayerControls = showEditOverlays,
                        onSelect = { onLayerSelect(LayerId.PoetName) },
                        onDrag = { onLayerDrag(LayerId.PoetName, it) },
                        alignment = Alignment.Center,
                        minWidth = minLayerWidth,
                    ) {
                        DirectionalTextField(
                            value = document.poetName.text,
                            onValueChange = onPoetNameChange,
                            readOnly = !isPoetEditing,
                            textStyle =
                                TextStyle(
                                    color = document.poetName.color,
                                    fontSize = (document.poetName.baseFontSizeSp + document.sizeProgress).sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = fontFamily,
                                ),
                            modifier =
                                Modifier
                                    .widthIn(min = minLayerWidth, max = maxLayerWidth)
                                    .wrapContentWidth(align = Alignment.CenterHorizontally),
                        )
                    }
                }

                if (document.sticker.visible) {
                    DraggableLayer(
                        offset = document.sticker.offset,
                        selected = document.selectedLayer == LayerId.Sticker,
                        showLayerControls = showEditOverlays,
                        onSelect = { onLayerSelect(LayerId.Sticker) },
                        onDrag = { onLayerDrag(LayerId.Sticker, it) },
                        minWidth = minLayerWidth,
                    ) {
                        StickerContent(document = document)
                    }
                }
            }
        }
    }
}

@Composable
private fun DirectionalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
) {
    val layoutDirection = textLayoutDirectionFor(value)
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            textStyle = textStyle.copy(textDirection = textDirectionFor(value)),
            modifier = modifier,
        )
    }
}

@Composable
private fun DraggableLayer(
    offset: abkabk.azbarkon.features.tasvir_negar.model.LayerOffset,
    selected: Boolean,
    showLayerControls: Boolean,
    onSelect: () -> Unit,
    onDrag: (abkabk.azbarkon.features.tasvir_negar.model.LayerOffset) -> Unit,
    alignment: Alignment = Alignment.Center,
    showTextControls: Boolean = false,
    isBold: Boolean = false,
    onTextGravityChange: ((TextGravity) -> Unit)? = null,
    onToggleBold: (() -> Unit)? = null,
    minWidth: Dp,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var dragDeltaPx by remember { mutableStateOf(Offset.Zero) }
    val currentOffset by rememberUpdatedState(offset)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val displaySelected = selected && showLayerControls

    LaunchedEffect(offset) {
        dragDeltaPx = Offset.Zero
    }

    val displayOffset =
        abkabk.azbarkon.features.tasvir_negar.model.LayerOffset(
            x = offset.x + with(density) { dragDeltaPx.x.toDp() },
            y = offset.y + with(density) { dragDeltaPx.y.toDp() },
        )

    fun commitDrag() {
        if (dragDeltaPx != Offset.Zero) {
            currentOnDrag(
                abkabk.azbarkon.features.tasvir_negar.model.LayerOffset(
                    x = currentOffset.x + with(density) { dragDeltaPx.x.toDp() },
                    y = currentOffset.y + with(density) { dragDeltaPx.y.toDp() },
                ),
            )
        }
        dragDeltaPx = Offset.Zero
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = alignment,
    ) {
        Column(
            modifier =
                Modifier
                    .graphicsLayer {
                        translationX = displayOffset.x.toPx()
                        translationY = displayOffset.y.toPx()
                    }
                    .wrapContentWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .widthIn(min = minWidth)
                        .wrapContentWidth()
                        .padding(4.dp)
                        .then(if (displaySelected) Modifier.border(1.dp, Color.White) else Modifier)
                        .padding(8.dp)
                        .then(
                            if (showLayerControls) {
                                Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures { onSelect() }
                                    }.pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragStart = {
                                                onSelect()
                                                dragDeltaPx = Offset.Zero
                                            },
                                            onDragEnd = { commitDrag() },
                                            onDragCancel = { dragDeltaPx = Offset.Zero },
                                        ) { change, dragAmount ->
                                            change.consume()
                                            dragDeltaPx += dragAmount
                                        }
                                    }
                            } else {
                                Modifier
                            },
                        ),
            ) {
                content()
            }

            if (displaySelected) {
                val showFormattingControls =
                    showTextControls && onTextGravityChange != null && onToggleBold != null
                if (showFormattingControls) {
                    TextFormattingBar(
                        isBold = isBold,
                        onTextGravityChange = onTextGravityChange ?: {},
                        onToggleBold = onToggleBold ?: {},
                    )
                }
            }
        }
    }
}

@Composable
private fun TextFormattingBar(
    isBold: Boolean,
    onTextGravityChange: (TextGravity) -> Unit,
    onToggleBold: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FormattingIcon(
            drawable = Res.drawable.ic_bold,
            tint = if (isBold) secondary else Color.White,
            onClick = onToggleBold,
        )
        FormattingIcon(Res.drawable.ic_align_left) { onTextGravityChange(TextGravity.Start) }
        FormattingIcon(Res.drawable.ic_align_center) { onTextGravityChange(TextGravity.Center) }
        FormattingIcon(Res.drawable.ic_align_right) { onTextGravityChange(TextGravity.End) }
    }
}

@Composable
private fun FormattingIcon(
    drawable: org.jetbrains.compose.resources.DrawableResource,
    tint: Color = Color.White,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick, modifier = Modifier.size(28.dp)) {
        Icon(
            painter = painterResource(drawable),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun StickerContent(document: EditorDocument) {
    val sticker = document.sticker
    val sizeModifier = Modifier.size(sticker.baseSizeDp + (document.sizeProgress * 4).dp)
    val colorFilter = sticker.colorFilter?.let { ColorFilter.tint(it) }

    when {
        sticker.galleryUri != null -> {
            LocalGalleryImage(
                uri = sticker.galleryUri,
                modifier = sizeModifier,
                contentScale = ContentScale.Fit,
            )
        }
        sticker.assetId != null -> {
            Image(
                painter = tasvirNegarPainter(sticker.assetId),
                contentDescription = null,
                modifier = sizeModifier,
                colorFilter = colorFilter,
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun CanvasBackground(
    document: EditorDocument,
    onBackgroundTap: () -> Unit,
) {
    val tapModifier =
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { onBackgroundTap() }
            }

    when (val background = document.background) {
        is EditorBackground.None -> {
            Box(modifier = tapModifier)
        }
        is EditorBackground.SolidColor -> {
            Box(
                modifier =
                    tapModifier.background(background.color),
            )
        }
        is EditorBackground.GalleryImage -> {
            LocalGalleryImage(
                uri = background.uri,
                modifier = tapModifier,
                contentScale = ContentScale.Crop,
            )
        }
        is EditorBackground.CatalogTexture -> {
            Image(
                painter = tasvirNegarPainter(background.drawableName),
                contentDescription = null,
                modifier = tapModifier,
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun DividerImage(
    assetId: String,
    width: Dp,
    colorFilter: Color?,
    flipVertical: Boolean,
) {
    Image(
        painter = tasvirNegarPainter(assetId),
        contentDescription = null,
        modifier =
            Modifier
                .width(width)
                .then(
                    if (flipVertical) {
                        Modifier.graphicsLayer(scaleY = -1f)
                    } else {
                        Modifier
                    },
                ),
        colorFilter = colorFilter?.let { ColorFilter.tint(it) },
        contentScale = ContentScale.FillWidth,
    )
}

@Composable
private fun AlignmentGrid(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val thirdW = size.width / 3f
        val thirdH = size.height / 3f
        val gridColor = Color.White.copy(alpha = 0.35f)
        for (i in 1..2) {
            drawLine(
                color = gridColor,
                start = Offset(thirdW * i, 0f),
                end = Offset(thirdW * i, size.height),
                strokeWidth = 1f,
            )
            drawLine(
                color = gridColor,
                start = Offset(0f, thirdH * i),
                end = Offset(size.width, thirdH * i),
                strokeWidth = 1f,
            )
        }
    }
}
