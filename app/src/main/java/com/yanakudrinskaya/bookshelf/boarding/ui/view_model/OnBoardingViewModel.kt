package com.yanakudrinskaya.bookshelf.boarding.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanakudrinskaya.bookshelf.boarding.domain.impl.OnBoardingContentUseCase
import com.yanakudrinskaya.bookshelf.boarding.ui.model.BoardingNavigation

class OnBoardingViewModel (
    private val onBoardingUseCase: OnBoardingContentUseCase
) : ViewModel() {

    private val boardingNavigationLiveData = MutableLiveData<BoardingNavigation>()
    fun getBoardingNavigationLiveData(): LiveData<BoardingNavigation> = boardingNavigationLiveData

    private lateinit var welcomeTextList: List<String>
    private var currentTextIndex = 0

    init {
        getContent()
    }

    private fun getContent() {
        welcomeTextList = onBoardingUseCase.getContent()
        boardingNavigationLiveData.value = BoardingNavigation.Content(welcomeTextList[0])
    }

    fun getNextText() {
        if (currentTextIndex < welcomeTextList.size - 1) {
            currentTextIndex++
            boardingNavigationLiveData.value = BoardingNavigation.Content(welcomeTextList[currentTextIndex])
        } else {
            boardingNavigationLiveData.value = BoardingNavigation.Close
        }
    }
}