package abkabk.azbarkon.features.tasvir_negar.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.tasvir_divider_1
import azbarkoncmp.shared.generated.resources.tasvir_divider_2
import azbarkoncmp.shared.generated.resources.tasvir_divider_3
import azbarkoncmp.shared.generated.resources.tasvir_divider_4
import azbarkoncmp.shared.generated.resources.tasvir_shape_poet
import azbarkoncmp.shared.generated.resources.tasvir_sticker_animal_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_animal_2
import azbarkoncmp.shared.generated.resources.tasvir_sticker_bird_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_bird_2
import azbarkoncmp.shared.generated.resources.tasvir_sticker_boat_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_boat_2
import azbarkoncmp.shared.generated.resources.tasvir_sticker_boat_3
import azbarkoncmp.shared.generated.resources.tasvir_sticker_boat_4
import azbarkoncmp.shared.generated.resources.tasvir_sticker_cloud_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_cloud_2
import azbarkoncmp.shared.generated.resources.tasvir_sticker_cloud_3
import azbarkoncmp.shared.generated.resources.tasvir_sticker_cloud_4
import azbarkoncmp.shared.generated.resources.tasvir_sticker_cloud_5
import azbarkoncmp.shared.generated.resources.tasvir_sticker_coffee_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_coffee_2
import azbarkoncmp.shared.generated.resources.tasvir_sticker_decorative_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_decorative_2
import azbarkoncmp.shared.generated.resources.tasvir_sticker_decorative_3
import azbarkoncmp.shared.generated.resources.tasvir_sticker_human_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_human_2
import azbarkoncmp.shared.generated.resources.tasvir_sticker_leaf_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_leaf_2
import azbarkoncmp.shared.generated.resources.tasvir_sticker_leaf_3
import azbarkoncmp.shared.generated.resources.tasvir_sticker_light_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_light_2
import azbarkoncmp.shared.generated.resources.tasvir_sticker_moon_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_phone_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_snow_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_tree_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_tree_2
import azbarkoncmp.shared.generated.resources.tasvir_sticker_tree_3
import azbarkoncmp.shared.generated.resources.tasvir_sticker_window_1
import azbarkoncmp.shared.generated.resources.tasvir_sticker_window_2
import org.jetbrains.compose.resources.painterResource

@Composable
fun tasvirNegarPainter(drawableName: String): Painter = painterResource(
    when (drawableName) {
        "bird_1" -> Res.drawable.tasvir_sticker_bird_1
        "bird_2" -> Res.drawable.tasvir_sticker_bird_2
        "leaf_1" -> Res.drawable.tasvir_sticker_leaf_1
        "leaf_2" -> Res.drawable.tasvir_sticker_leaf_2
        "leaf_3" -> Res.drawable.tasvir_sticker_leaf_3
        "tree_1" -> Res.drawable.tasvir_sticker_tree_1
        "tree_2" -> Res.drawable.tasvir_sticker_tree_2
        "tree_3" -> Res.drawable.tasvir_sticker_tree_3
        "light_1" -> Res.drawable.tasvir_sticker_light_1
        "light_2" -> Res.drawable.tasvir_sticker_light_2
        "window_1" -> Res.drawable.tasvir_sticker_window_1
        "window_2" -> Res.drawable.tasvir_sticker_window_2
        "cloud_1" -> Res.drawable.tasvir_sticker_cloud_1
        "cloud_2" -> Res.drawable.tasvir_sticker_cloud_2
        "cloud_3" -> Res.drawable.tasvir_sticker_cloud_3
        "cloud_4" -> Res.drawable.tasvir_sticker_cloud_4
        "cloud_5" -> Res.drawable.tasvir_sticker_cloud_5
        "moon_1" -> Res.drawable.tasvir_sticker_moon_1
        "snow_1" -> Res.drawable.tasvir_sticker_snow_1
        "boat_1" -> Res.drawable.tasvir_sticker_boat_1
        "boat_2" -> Res.drawable.tasvir_sticker_boat_2
        "boat_3" -> Res.drawable.tasvir_sticker_boat_3
        "boat_4" -> Res.drawable.tasvir_sticker_boat_4
        "animal_1" -> Res.drawable.tasvir_sticker_animal_1
        "animal_2" -> Res.drawable.tasvir_sticker_animal_2
        "coffee_1" -> Res.drawable.tasvir_sticker_coffee_1
        "coffee_2" -> Res.drawable.tasvir_sticker_coffee_2
        "human_1" -> Res.drawable.tasvir_sticker_human_1
        "human_2" -> Res.drawable.tasvir_sticker_human_2
        "phone_1" -> Res.drawable.tasvir_sticker_phone_1
        "decorative_1" -> Res.drawable.tasvir_sticker_decorative_1
        "decorative_2" -> Res.drawable.tasvir_sticker_decorative_2
        "decorative_3" -> Res.drawable.tasvir_sticker_decorative_3
        "divider_1" -> Res.drawable.tasvir_divider_1
        "divider_2" -> Res.drawable.tasvir_divider_2
        "divider_3" -> Res.drawable.tasvir_divider_3
        "divider_4" -> Res.drawable.tasvir_divider_4
        "shape_poet" -> Res.drawable.tasvir_shape_poet
        else -> Res.drawable.tasvir_shape_poet
    },
)
