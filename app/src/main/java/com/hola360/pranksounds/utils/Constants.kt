package com.hola360.pranksounds.utils

import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Prank
import com.hola360.pranksounds.data.model.SoundCategory

object Constants {
    const val PREFERENCE_NAME = "PRANK_SOUNDS"
    const val ALARM_SERVICE_ACTION = "com.hola360.pranksounds.ACTION_CALL"
    const val CHANNEL_ID = 101
    const val AUDIO_EXTENSION = "mp3"
    const val FOLDER_PATH = "PranSounds"
    const val FILE_NAME_FILTER = "?\"*"
    const val SEEKBAR_PADDING = 70
    const val MIN_CLICK_INTERVAL = 1000
    //list of recycler view in home screen
    val PRANK_LIST = listOf(
        Prank(1, R.drawable.ic_hair_cutting, R.drawable.bg_action_home_hair_cutting, "Hair Cutting"),
        Prank(2, R.drawable.ic_broken_screen, R.drawable.bg_action_home_broken, "Broken Screen"),
        Prank(3, R.drawable.ic_call_screen, R.drawable.bg_action_home_call_screen, "Call Screen"),
        Prank(4, R.drawable.ic_sound_funny, R.drawable.bg_action_home_sound_funny, "Sound Funny"),
        Prank(5, R.drawable.ic_taser_prank, R.drawable.bg_action_home_broken_taser, "Taser Prank"),
        Prank(6, R.drawable.ic_setting, R.drawable.bg_action_home_setting, "Setting")
    )

    //images of banner in home screen
    val BANNER_IMAGE = listOf(
        R.drawable.banner_1,
        R.drawable.banner_2,
        R.drawable.banner_3,
        R.drawable.banner_4,
    )

    val THUMB_RES = listOf(
        R.drawable.thumb_1,
        R.drawable.thumb_2,
        R.drawable.thumb_3,
        R.drawable.thumb_4,
        R.drawable.thumb_5,
        R.drawable.thumb_6,
        R.drawable.thumb_7,
        R.drawable.thumb_8,
    )

    //delay for update seekbar
    const val DELAY_UPDATE = 100L

    //minimum sound duration
    const val MIN_SOUND_DURATION = 1000

    //favorite category item
    val FAVORITE_CATEGORY = SoundCategory("Favorite Sound", "fav_sound", "thumb_cat/pro_7.webp")
    const val FAVORITE_ID = "fav_sound"

    //timing of splash screen before navigate to another screen
    const val SPLASH_TIMING = 200L

    //timing duration between twice back pressed to quit app
    const val BACK_PRESSED_TIMING = 2000L

    //base URL of API
    const val BASE_URL = "https://devops.hola360.com/meme_sound/"

    //sub url for images and sound
    const val SUB_URL = "https://files.newmobile.rocks/meme-sound/"
    const val SOUND_FUNNY_PATH = "/soundFuny.php"

    // call sub url
    const val CALL_SUB_PATH = "soundFunnyDev.php"

    // request code in pending intent
    const val REQUEST_CODE = "REQUEST_CODE"
    const val INTENT_CODE = "INTENT_CODE"

    //sound category parameter
    const val SOUND_CAT_PARAM = "list_category"
    const val CAT_DETAIL_PARAM = "detail_category"
    const val SOUND_ITEM_PER_PAGE = 10

    const val DIR_PATH = "/PranSounds/"

    //CALLING SCREEN
    //calling ringtone
    const val CALLING_RINGTONE =
        "https://nf1f8200-a.akamaihd.net/downloads/ringtones/files/mp3/7120-download-iphone-6-original-ringtone-42676.mp3"
    //calling time format
    const val CALLING_TIME_FORMAT = "%02d:%02d"
    //wave animation duration
    const val WAVE_ANIM_DURATION = 2000L
    //default background color off calling screen
    const val DEFAULT_BACKGROUND_COLOR = -3491739
    const val DEFAULT_GRADIENT_START_COLOR = -1603804
    const val DEFAULT_GRADIENT_MID_COLOR = -4934297
    const val DEFAULT_GRADIENT_END_COLOR = -1603545
    //delay update time in calling screen
    const val DELAY_CALLING_TIME = 1000L

    //submit api attribute
    const val SUBMIT_TYPE = "update_count_sound"
    const val SUBMIT_LIKE_TYPE = "like"
    const val SUBMIT_DOWNLOAD_TYPE = "download"
}