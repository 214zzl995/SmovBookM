package com.leri.smovbook.network

object Api {
    const val BASE_URL = "https://gist.githubusercontent.com/"
    const val PAGING_SIZE = 20

    @JvmStatic
    fun getPosterPath(posterPath: String?): String {
        return BASE_URL + posterPath
    }

    @JvmStatic
    fun getBackdropPath(backdropPath: String?): String {
        return BASE_URL + backdropPath
    }

    @JvmStatic
    fun getYoutubeVideoPath(videoPath: String?): String {
        return BASE_URL + videoPath
    }

    @JvmStatic
    fun getYoutubeThumbnailPath(thumbnailPath: String?): String {
        return "$BASE_URL$thumbnailPath/default.jpg"
    }
}
