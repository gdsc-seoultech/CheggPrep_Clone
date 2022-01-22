package com.comye1.cheggprep.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.SampleDataSet
import com.comye1.cheggprep.models.Deck

class HomeViewModel : ViewModel() {

    var myDeckList = mutableStateListOf<Deck>()
        private set

    init {
        myDeckList = SampleDataSet.myDeckSample.toMutableStateList()
    }
}