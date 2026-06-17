package abkabk.azbarkon.features.tasvir_negar.model

import androidx.compose.ui.graphics.Color

object TasvirNegarCatalog {
    val colorOptions: List<ColorOption> =
        listOf(
            ColorOption(isCustomPicker = true),
            ColorOption(color = Color.White),
            ColorOption(color = Color.Black),
            ColorOption(color = Color(0xFFFFD700)),
            ColorOption(color = Color(0xFFC0392B)),
            ColorOption(color = Color(0xFF457367)),
            ColorOption(color = Color(0xFF3498DB)),
            ColorOption(color = Color(0xFFE4D6C3)),
            ColorOption(color = Color(0xFF355B50)),
            ColorOption(color = Color(0xFF6C4D36)),
            ColorOption(color = Color(0xFFA15045)),
        )

    val stickers: List<CatalogItem> =
        listOf(
            CatalogItem("bird_1", "tasvir_sticker_bird_1"),
            CatalogItem("bird_2", "tasvir_sticker_bird_2"),
            CatalogItem("leaf_1", "tasvir_sticker_leaf_1"),
            CatalogItem("leaf_2", "tasvir_sticker_leaf_2"),
            CatalogItem("leaf_3", "tasvir_sticker_leaf_3"),
            CatalogItem("tree_1", "tasvir_sticker_tree_1"),
            CatalogItem("tree_2", "tasvir_sticker_tree_2"),
            CatalogItem("tree_3", "tasvir_sticker_tree_3"),
            CatalogItem("light_1", "tasvir_sticker_light_1"),
            CatalogItem("light_2", "tasvir_sticker_light_2"),
            CatalogItem("window_1", "tasvir_sticker_window_1"),
            CatalogItem("window_2", "tasvir_sticker_window_2"),
            CatalogItem("cloud_1", "tasvir_sticker_cloud_1"),
            CatalogItem("cloud_2", "tasvir_sticker_cloud_2"),
            CatalogItem("cloud_3", "tasvir_sticker_cloud_3"),
            CatalogItem("cloud_4", "tasvir_sticker_cloud_4"),
            CatalogItem("cloud_5", "tasvir_sticker_cloud_5"),
            CatalogItem("moon_1", "tasvir_sticker_moon_1"),
            CatalogItem("snow_1", "tasvir_sticker_snow_1"),
            CatalogItem("boat_1", "tasvir_sticker_boat_1"),
            CatalogItem("boat_2", "tasvir_sticker_boat_2"),
            CatalogItem("boat_3", "tasvir_sticker_boat_3"),
            CatalogItem("boat_4", "tasvir_sticker_boat_4"),
            CatalogItem("animal_1", "tasvir_sticker_animal_1"),
            CatalogItem("animal_2", "tasvir_sticker_animal_2"),
            CatalogItem("coffee_1", "tasvir_sticker_coffee_1"),
            CatalogItem("coffee_2", "tasvir_sticker_coffee_2"),
            CatalogItem("human_1", "tasvir_sticker_human_1"),
            CatalogItem("human_2", "tasvir_sticker_human_2"),
            CatalogItem("phone_1", "tasvir_sticker_phone_1"),
            CatalogItem("decorative_1", "tasvir_sticker_decorative_1"),
            CatalogItem("decorative_2", "tasvir_sticker_decorative_2"),
            CatalogItem("decorative_3", "tasvir_sticker_decorative_3"),
        )

    val dividers: List<CatalogItem> =
        listOf(
            CatalogItem("divider_1", "tasvir_divider_1"),
            CatalogItem("divider_2", "tasvir_divider_2"),
            CatalogItem("divider_3", "tasvir_divider_3"),
            CatalogItem("divider_4", "tasvir_divider_4"),
        )

    val shapeOptions: List<CatalogItem> =
        listOf(CatalogItem("shape_poet", "tasvir_shape_poet")) + stickers + dividers

    const val POET_SHAPE_INDEX = 0
    const val FIRST_STICKER_INDEX = 1

    val firstDividerIndex: Int get() = FIRST_STICKER_INDEX + stickers.size
}
