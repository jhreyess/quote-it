package com.quoteit.android.ui.utils

import com.quoteit.android.R

class LocalImages {
    companion object{
        private val images = listOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
        )

        fun getImages() = images
    }
}