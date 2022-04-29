package com.hola360.pranksounds.utils

import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Prank
import com.hola360.pranksounds.data.model.SoundCategory

object Constants {
    const val PREFERENCE_NAME = "PRANK_SOUNDS"

    //list of recycler view in home screen
    val PRANK_LIST = listOf(
        Prank(1, R.drawable.ic_hair_cutting, R.drawable.bg_hair_cutting, "Hair Cutting"),
        Prank(2, R.drawable.ic_broken_screen, R.drawable.bg_broken_screen, "Broken Screen"),
        Prank(3, R.drawable.ic_call_screen, R.drawable.bg_call_screen, "Call Screen"),
        Prank(4, R.drawable.ic_sound_funny, R.drawable.bg_sound_funny, "Sound Funny"),
        Prank(5, R.drawable.ic_taser_prank, R.drawable.bg_taser_prank, "Taser Prank"),
        Prank(6, R.drawable.ic_setting, R.drawable.bg_setting, "Setting")
    )

    //images of banner in home screen
    val BANNER_IMAGE = listOf(
        R.drawable.banner_1,
        R.drawable.banner_2,
        R.drawable.banner_3,
        R.drawable.banner_4,
    )

    //favorite category item
    val FAVORITE_CATEGORY = SoundCategory("Favorite Sound", "fav_sound", "thumb_cat/pro_7.webp")

    //timing of splash screen before navigate to another screen
    const val SPLASH_TIMING = 3000L

    //timing duration between twice back pressed to quit app
    const val BACK_PRESSED_TIMING = 2000L

    //base URL of API
    const val BASE_URL = "https://r22.newmobile.rocks"

    //sub url for images and sound
    const val SUB_URL = "https://files.newmobile.rocks/meme-sound/"
    //sound category parameter
    const val SOUND_CAT_PARAM = "list_category"
}