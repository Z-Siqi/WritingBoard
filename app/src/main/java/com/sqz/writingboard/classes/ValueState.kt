package com.sqz.writingboard.classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ValueState : ViewModel() {
    /*value*/
    var softKeyboard by mutableStateOf(false)
    var isEditing by mutableStateOf(false)
    var cleanAllText by mutableStateOf(false)
    var editButton by mutableStateOf(false)
    var readOnlyText by mutableStateOf(false)
    var editingHorizontalScreen by mutableStateOf(false)
    var readOnlyTextScroll by mutableStateOf(true)

    /*action on text*/
    var saveAction by mutableStateOf(false)
    var matchText by mutableStateOf(false)

    /*to change screen*/
    var updateScreen by mutableStateOf(false)
    var ee by mutableStateOf(false)

    /*for trigger multiple action*/
    var editAction by mutableStateOf(false)
    var doneAction by mutableStateOf(false)
    var onClickSetting by mutableStateOf(false)

    /*QSTileRequestResult value*/
    var resultOfQST by mutableIntStateOf(-5)
}