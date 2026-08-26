package abkabk.azbarkon.features.tasvirNegar.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.tasvir_divider_1
import sarv.shared.generated.resources.tasvir_divider_2
import sarv.shared.generated.resources.tasvir_divider_3
import sarv.shared.generated.resources.tasvir_divider_4
import sarv.shared.generated.resources.tasvir_shape_poet
import sarv.shared.generated.resources.tasvir_sticker_animal_1
import sarv.shared.generated.resources.tasvir_sticker_animal_2
import sarv.shared.generated.resources.tasvir_sticker_bird_1
import sarv.shared.generated.resources.tasvir_sticker_bird_2
import sarv.shared.generated.resources.tasvir_sticker_boat_1
import sarv.shared.generated.resources.tasvir_sticker_boat_2
import sarv.shared.generated.resources.tasvir_sticker_boat_3
import sarv.shared.generated.resources.tasvir_sticker_boat_4
import sarv.shared.generated.resources.tasvir_sticker_cloud_1
import sarv.shared.generated.resources.tasvir_sticker_cloud_2
import sarv.shared.generated.resources.tasvir_sticker_cloud_3
import sarv.shared.generated.resources.tasvir_sticker_cloud_4
import sarv.shared.generated.resources.tasvir_sticker_cloud_5
import sarv.shared.generated.resources.tasvir_sticker_coffee_1
import sarv.shared.generated.resources.tasvir_sticker_coffee_2
import sarv.shared.generated.resources.tasvir_sticker_decorative_1
import sarv.shared.generated.resources.tasvir_sticker_decorative_2
import sarv.shared.generated.resources.tasvir_sticker_decorative_3
import sarv.shared.generated.resources.tasvir_sticker_human_1
import sarv.shared.generated.resources.tasvir_sticker_human_2
import sarv.shared.generated.resources.tasvir_sticker_leaf_1
import sarv.shared.generated.resources.tasvir_sticker_leaf_2
import sarv.shared.generated.resources.tasvir_sticker_leaf_3
import sarv.shared.generated.resources.tasvir_sticker_light_1
import sarv.shared.generated.resources.tasvir_sticker_light_2
import sarv.shared.generated.resources.tasvir_sticker_moon_1
import sarv.shared.generated.resources.tasvir_sticker_phone_1
import sarv.shared.generated.resources.tasvir_sticker_snow_1
import sarv.shared.generated.resources.tasvir_sticker_tree_1
import sarv.shared.generated.resources.tasvir_sticker_tree_2
import sarv.shared.generated.resources.tasvir_sticker_tree_3
import sarv.shared.generated.resources.tasvir_sticker_window_1
import sarv.shared.generated.resources.tasvir_sticker_window_2
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val tasvirNegarResources: Map<String, DrawableResource> =
    mapOf(
        "bird_1" to Res.drawable.tasvir_sticker_bird_1,
        "bird_2" to Res.drawable.tasvir_sticker_bird_2,
        "leaf_1" to Res.drawable.tasvir_sticker_leaf_1,
        "leaf_2" to Res.drawable.tasvir_sticker_leaf_2,
        "leaf_3" to Res.drawable.tasvir_sticker_leaf_3,
        "tree_1" to Res.drawable.tasvir_sticker_tree_1,
        "tree_2" to Res.drawable.tasvir_sticker_tree_2,
        "tree_3" to Res.drawable.tasvir_sticker_tree_3,
        "light_1" to Res.drawable.tasvir_sticker_light_1,
        "light_2" to Res.drawable.tasvir_sticker_light_2,
        "window_1" to Res.drawable.tasvir_sticker_window_1,
        "window_2" to Res.drawable.tasvir_sticker_window_2,
        "cloud_1" to Res.drawable.tasvir_sticker_cloud_1,
        "cloud_2" to Res.drawable.tasvir_sticker_cloud_2,
        "cloud_3" to Res.drawable.tasvir_sticker_cloud_3,
        "cloud_4" to Res.drawable.tasvir_sticker_cloud_4,
        "cloud_5" to Res.drawable.tasvir_sticker_cloud_5,
        "moon_1" to Res.drawable.tasvir_sticker_moon_1,
        "snow_1" to Res.drawable.tasvir_sticker_snow_1,
        "boat_1" to Res.drawable.tasvir_sticker_boat_1,
        "boat_2" to Res.drawable.tasvir_sticker_boat_2,
        "boat_3" to Res.drawable.tasvir_sticker_boat_3,
        "boat_4" to Res.drawable.tasvir_sticker_boat_4,
        "animal_1" to Res.drawable.tasvir_sticker_animal_1,
        "animal_2" to Res.drawable.tasvir_sticker_animal_2,
        "coffee_1" to Res.drawable.tasvir_sticker_coffee_1,
        "coffee_2" to Res.drawable.tasvir_sticker_coffee_2,
        "human_1" to Res.drawable.tasvir_sticker_human_1,
        "human_2" to Res.drawable.tasvir_sticker_human_2,
        "phone_1" to Res.drawable.tasvir_sticker_phone_1,
        "decorative_1" to Res.drawable.tasvir_sticker_decorative_1,
        "decorative_2" to Res.drawable.tasvir_sticker_decorative_2,
        "decorative_3" to Res.drawable.tasvir_sticker_decorative_3,
        "divider_1" to Res.drawable.tasvir_divider_1,
        "divider_2" to Res.drawable.tasvir_divider_2,
        "divider_3" to Res.drawable.tasvir_divider_3,
        "divider_4" to Res.drawable.tasvir_divider_4,
        "shape_poet" to Res.drawable.tasvir_shape_poet,
    )

@Composable
fun tasvirNegarPainter(drawableName: String): Painter =
    painterResource(tasvirNegarResources[drawableName] ?: Res.drawable.tasvir_shape_poet)
