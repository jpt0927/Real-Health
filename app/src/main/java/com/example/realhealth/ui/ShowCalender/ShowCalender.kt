package com.example.realhealth.ui.ShowCalender

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.realhealth.R
import java.util.Calendar

enum class CalenderPage {
    Days, Months, Years, TextBox
}


@Composable
fun CalenderMain(DisplayDate: String, width: Int, height: Int, color: Color, modifier: Modifier = Modifier) {
    var CalenderImage: CalenderPage by remember { mutableStateOf(CalenderPage.Days) }
    var DisplayDate: String by remember { mutableStateOf(DisplayDate) }

    val calender = java.util.Calendar.getInstance()
    var CurrentDate: String by remember { mutableStateOf(String.format("%04d", calender.get(Calendar.YEAR)) + "." + String.format("%02d", calender.get(Calendar.MONTH) + 1) + "." + String.format("%02d", calender.get(Calendar.DAY_OF_MONTH))) }

    val MonthMinusOne = {
        var year = DisplayDate.slice(0..3).toInt()
        var month = DisplayDate.slice(5..6).toInt()
        var day = DisplayDate.slice(8..9).toInt()
        val calender = Calendar.getInstance()
        calender.set(year, month-1, day)
        calender.add(Calendar.MONTH, -1)
        year = calender.get(Calendar.YEAR)
        month = calender.get(Calendar.MONTH) + 1
        day = calender.get(Calendar.DAY_OF_MONTH)
        DisplayDate = String.format("%04d", year) + "." + String.format("%02d", month) + "." + String.format("%02d", day)
    }

    val MonthPlusOne = {
        var year = DisplayDate.slice(0..3).toInt()
        var month = DisplayDate.slice(5..6).toInt()
        var day = DisplayDate.slice(8..9).toInt()
        val calender = Calendar.getInstance()
        calender.set(year, month-1, day)
        calender.add(Calendar.MONTH, 1)
        year = calender.get(Calendar.YEAR)
        month = calender.get(Calendar.MONTH) + 1
        day = calender.get(Calendar.DAY_OF_MONTH)
        DisplayDate = String.format("%04d", year) + "." + String.format("%02d", month) + "." + String.format("%02d", day)
    }

    val CurrentDateUpdate = { NewDate: String ->
        CurrentDate = NewDate
    }

    CalenderDays(
        DisplayDate = DisplayDate,
        CurrentDate = CurrentDate,
        width = width,
        height = height,
        MonthMinusOne = MonthMinusOne,
        MonthPlusOne = MonthPlusOne,
        CurrentDateUpdate = CurrentDateUpdate,
        color = color
    )
}

fun getFirstDayOfWeek(year: Int, month: Int): Int {
    val calender = Calendar.getInstance()
    calender.set(year, month-1, 1)
    return calender.get(Calendar.DAY_OF_WEEK)
}

fun getLastDayOfWeek(year: Int, month: Int): Int {
    val calender = Calendar.getInstance()
    calender.set(year, month-1, 1)
    val day = calender.getActualMaximum(Calendar.DAY_OF_MONTH)
    calender.set(year, month-1, day)
    return calender.get(Calendar.DAY_OF_WEEK)
}

fun getNumOfWeek(year: Int, month: Int, FirstDoW: Int, LastDoW: Int): Int {
    val calender = Calendar.getInstance()
    calender.set(year, month-1, 1)
    val day = calender.getActualMaximum(Calendar.DAY_OF_MONTH)
    calender.set(year, month-1, day)
    val result = (day - ((7 - FirstDoW + 1) + LastDoW)) / 7 + 2
    return result
}

@Composable
fun CalenderDays(DisplayDate: String, CurrentDate: String, width: Int, height: Int, modifier: Modifier = Modifier, color: Color,
                 MonthMinusOne: () -> Unit, MonthPlusOne: () -> Unit, CurrentDateUpdate: (String) -> Unit) {
    val year = DisplayDate.slice(0..3).toInt()
    val month = DisplayDate.slice(5..6).toInt()
    val day = DisplayDate.slice(8..9).toInt()

    val curyear = CurrentDate.slice(0..3).toInt()
    val curmonth = CurrentDate.slice(5..6).toInt()
    val curday = CurrentDate.slice(8..9).toInt()

    val FirstDoW = getFirstDayOfWeek(year, month)
    val LastDoW = getLastDayOfWeek(year, month)
    val NumOfWeek = getNumOfWeek(year, month, FirstDoW, LastDoW)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(width.dp).height(height.dp)
            .background(color = Color.White)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height((height*40/304).dp)
        ) {
            Box(modifier = Modifier.width((width*35/349).dp)) {}
            Box(
                modifier = Modifier.width((width*20/349).dp)
                    .clickable(onClick = MonthMinusOne)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "<",
                    fontSize = (height*20/304).sp
                )
            }
            Box(
                modifier = Modifier.width((width*239/349).dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = DisplayDate.slice(0..6),
                    fontSize = (height*18/304).sp
                )
            }
            Box(modifier = Modifier.width((width*20/349).dp)
                .clickable(onClick = MonthPlusOne)) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = ">",
                    fontSize = (height*20/304).sp
                )
            }
            Box(modifier = Modifier.width((width*35/349).dp)) {}
        }
        Box(
            modifier = Modifier.height((height*8/304).dp)
        ) {
            Box(
                modifier = Modifier.height((height*3/304).dp)
                    .width((width*300/349).dp)
                    .background(color = Color(0xFFD9D9D9))
            )
        }
        Column(
            modifier = Modifier.height((height*230/304).dp)
                .width((width*330/349).dp)
                .background(color = Color.White)
        ) {
            //달력 내부 ToDO
            Row(
                modifier = Modifier.height((height*15/304).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..7) {
                    val textDate = when (i) {
                        1 -> R.string.sunday
                        2 -> R.string.monday
                        3 -> R.string.tuesday
                        4 -> R.string.wednesday
                        5 -> R.string.thursday
                        6 -> R.string.friday
                        7 -> R.string.saturday
                        else -> R.string.sunday
                    }
                    Box(
                        modifier = Modifier.width((width*330/7/349).dp)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(id = textDate),
                            fontSize = (height * 10 / 304).sp
                        )
                    }
                }
            }
            Box(
                modifier = Modifier.height((height*10/304).dp)
            )
            LazyColumn(
                userScrollEnabled = false
            ) {
                items(NumOfWeek) { index ->
                    when (index) {
                        0 -> Row(
                            modifier = Modifier.height((height*205/304/NumOfWeek).dp)
                        ) {
                            for (i in 1..FirstDoW-1) {
                                Box(modifier = Modifier.size((width*330/7/349).dp)) {
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text = "",
                                    )
                                }
                            }
                            for (i in FirstDoW..7) {
                                Box(
                                    modifier = Modifier.size((width*330/7/349).dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier.size((height*205/304/NumOfWeek*0.9).dp)
                                            .clip(CircleShape)
                                            .background(color = if ((year == curyear) and (month == curmonth) and (i+1-FirstDoW == curday)) color else Color.White)
                                            .clickable() {
                                                CurrentDateUpdate(String.format("%04d", year) + "." + String.format("%02d", month) + "." + String.format("%02d", i+1-FirstDoW))
                                            }
                                    )
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text = "${i+1-FirstDoW}",
                                        fontSize = (height*205/304/NumOfWeek*0.5).sp,
                                        color = if ((year == curyear) and (month == curmonth) and (i+1-FirstDoW == curday)) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                        NumOfWeek-1 -> Row(
                            modifier = Modifier.height((height*205/304/NumOfWeek).dp)
                        ) {
                            val day = 8 - FirstDoW + (7 * (index-1))
                            for (i in 1..LastDoW) {
                                Box(
                                    modifier = Modifier.size((width*330/7/349).dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier.size((height*205/304/NumOfWeek*0.9).dp)
                                            .clip(CircleShape)
                                            .background(color = if ((year == curyear) and (month == curmonth) and (day+i == curday)) color else Color.White)
                                            .clickable() {
                                                CurrentDateUpdate(String.format("%04d", year) + "." + String.format("%02d", month) + "." + String.format("%02d", day+i))
                                            }
                                    )
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text = "${day+i}",
                                        fontSize = (height*205/304/NumOfWeek*0.5).sp,
                                        color = if ((year == curyear) and (month == curmonth) and (day+i == curday)) Color.White else Color.Black
                                    )
                                }
                            }
                            for (i in LastDoW+1..7) {
                                Box(
                                    modifier = Modifier.size((width*330/7/349).dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text = "",
                                    )
                                }
                            }
                        }
                        else -> Row(
                            modifier = Modifier.height((height*205/304/NumOfWeek).dp)
                        ) {
                            val day = 8 - FirstDoW + (7 * (index-1))
                            for (i in 1..7) {
                                Box(
                                    modifier = Modifier.size((width*330/7/349).dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier.size((height*205/304/NumOfWeek*0.9).dp)
                                        .clip(CircleShape)
                                        .background(color = if ((year == curyear) and (month == curmonth) and (day+i == curday)) color else Color.White)
                                        .clickable() {
                                            CurrentDateUpdate(String.format("%04d", year) + "." + String.format("%02d", month) + "." + String.format("%02d", day+i))
                                        }
                                    )
                                    Text(
                                        modifier = Modifier.align(Alignment.Center),
                                        text = "${day+i}",
                                        fontSize = (height*205/304/NumOfWeek*0.5).sp,
                                        color = if ((year == curyear) and (month == curmonth) and (day+i == curday)) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalenderMonths(CurrentDate: String, width: Int, height: Int, modifier: Modifier = Modifier) {
}

@Composable
fun CalenderYears(CurrentDate: String, width: Int, height: Int, modifier: Modifier = Modifier) {
}

@Composable
fun CalenderTextBox(CurrentDate: String, width: Int, height: Int, modifier: Modifier = Modifier) {
}


@Preview
@Composable
fun previewMain() {
    CalenderMain("2025.07.05", 349, 304, Color(0xFF1294F2))
}