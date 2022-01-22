package com.comye1.cheggprep.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CreateViewModel: ViewModel() {

    // CreateScreen /////////////////
    var createScreenState = mutableStateOf(CreateState.TitleScreen)
        private set



    fun toCardScreen() {
        createScreenState.value = CreateState.CardScreen
    }

    fun toTitleScreen() {
        createScreenState.value = CreateState.TitleScreen
    }
}