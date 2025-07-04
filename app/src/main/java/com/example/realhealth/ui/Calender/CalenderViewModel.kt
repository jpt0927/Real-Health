package com.example.realhealth.ui.Calender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalenderViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Calender Fragment"
    }
    val text: LiveData<String> = _text
}