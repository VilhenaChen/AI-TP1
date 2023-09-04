package pt.vilhena.ai.trabalhopratico.data.common

import pt.vilhena.ai.trabalhopratico.R

enum class ActivitiesEnum(val activity: String, val stringResource: Int, val animationResource: Int) {
    WALKING("walking", R.string.walking, R.raw.walking_animation),
    STANDING("standing", R.string.standing, R.raw.standing_animation),
    CLIMBING_UP_STAIRS("upstairs", R.string.climbing_up_stairs, R.raw.climbing_up_stairs_animation),
    CLIMBING_DOWN_STAIRS("downstairs", R.string.climbing_down_stairs, R.raw.climbing_down_stairs_animation),
}
