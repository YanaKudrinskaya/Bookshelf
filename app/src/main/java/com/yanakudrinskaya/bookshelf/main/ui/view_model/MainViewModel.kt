package com.yanakudrinskaya.bookshelf.main.ui.view_model

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.yanakudrinskaya.bookshelf.login.domain.UserProfileInteractor
import com.yanakudrinskaya.bookshelf.login.ui.activity.LoginActivity

class MainViewModel (
    private var userProfileInteractor: UserProfileInteractor,
) : ViewModel() {

}