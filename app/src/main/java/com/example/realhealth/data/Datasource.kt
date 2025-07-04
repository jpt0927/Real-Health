package com.example.realhealth.data

import com.example.realhealth.R
import com.example.realhealth.model.Affirmation

class Datasource() {
    fun loadAffirmations(): List<Affirmation> {
        return listOf<Affirmation>(
            Affirmation(R.string.data1, R.drawable.dataimage1, 0),
            Affirmation(R.string.data1, R.drawable.dataimage1, 1),
            Affirmation(R.string.data1, R.drawable.dataimage1, 2),
            Affirmation(R.string.data1, R.drawable.dataimage1, 3),
            Affirmation(R.string.data1, R.drawable.dataimage1, 4),
            Affirmation(R.string.data1, R.drawable.dataimage1, 5),
            Affirmation(R.string.data1, R.drawable.dataimage1, 6),
            Affirmation(R.string.data1, R.drawable.dataimage1, 7),
            Affirmation(R.string.data1, R.drawable.dataimage1, 8),
            Affirmation(R.string.data1, R.drawable.dataimage1, 9),
            Affirmation(R.string.data1, R.drawable.dataimage1, 10),
            Affirmation(R.string.data1, R.drawable.dataimage1, 11),
            Affirmation(R.string.data1, R.drawable.dataimage1, 12),
            Affirmation(R.string.data1, R.drawable.dataimage1, 13),
            Affirmation(R.string.data1, R.drawable.dataimage1, 14),
            Affirmation(R.string.data1, R.drawable.dataimage1, 15),
            Affirmation(R.string.data1, R.drawable.dataimage1, 16),
            Affirmation(R.string.data1, R.drawable.dataimage1, 17),
            Affirmation(R.string.data1, R.drawable.dataimage1, 18),
            Affirmation(R.string.data1, R.drawable.dataimage1, 19),
        )
    }
}