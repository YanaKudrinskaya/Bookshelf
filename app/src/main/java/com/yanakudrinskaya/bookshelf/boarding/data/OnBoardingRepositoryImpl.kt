package com.yanakudrinskaya.bookshelf.boarding.data

import android.content.Context
import com.yanakudrinskaya.bookshelf.R
import com.yanakudrinskaya.bookshelf.boarding.domain.OnBoardingRepository

class OnBoardingRepositoryImpl(
    val context: Context
) : OnBoardingRepository {
    override fun getContent(): List<String> {

        return arrayListOf(
            context.getString(R.string.on_boarding_text_1),
            context.getString(R.string.on_boarding_text_2),
            context.getString(R.string.on_boarding_text_3)
        )
    }
}