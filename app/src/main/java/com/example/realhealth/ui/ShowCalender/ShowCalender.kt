package com.example.realhealth.ui.ShowCalender

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.realhealth.R
import java.util.Calendar

enum class CalenderPage {
    Days, Months, Years
}


@Composable
fun CalenderMain(currentDate: String = "no", currentDateUpdate: (String) -> Unit = {},
    width: Int, height: Int, color: Color, modifier: Modifier = Modifier) {
    var CalenderImage: CalenderPage by remember { mutableStateOf(CalenderPage.Days) }

    val calender = java.util.Calendar.getInstance()
    var DisplayDate: String by remember { mutableStateOf(String.format("%04d", calender.get(Calendar.YEAR)) + "." + String.format("%02d", calender.get(Calendar.MONTH) + 1) + "." + String.format("%02d", calender.get(Calendar.DAY_OF_MONTH))) }
    var CurrentDate: String by remember { mutableStateOf(if (currentDate == "no") (String.format("%04d", calender.get(Calendar.YEAR)) + "." + String.format("%02d", calender.get(Calendar.MONTH) + 1) + "." + String.format("%02d", calender.get(Calendar.DAY_OF_MONTH))) else currentDate) }

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
        currentDateUpdate(NewDate)
    }

    val ChangeMonth = { month: Int ->
        val year = DisplayDate.slice(0..3).toInt()
        val day = DisplayDate.slice(8..9).toInt()
        DisplayDate = String.format("%04d", year) + "." + String.format("%02d", month) + "." + String.format("%02d", day)
        CalenderImage = CalenderPage.Days
    }

    val ChangeYear = { year: Int ->
        val month = DisplayDate.slice(5..6).toInt()
        val day = DisplayDate.slice(8..9).toInt()
        DisplayDate = String.format("%04d", year) + "." + String.format("%02d", month) + "." + String.format("%02d", day)
        CalenderImage = CalenderPage.Months
    }

    val YearMinusOne = {
        var year = DisplayDate.slice(0..3).toInt()
        var month = DisplayDate.slice(5..6).toInt()
        var day = DisplayDate.slice(8..9).toInt()
        val calender = Calendar.getInstance()
        calender.set(year, month-1, day)
        calender.add(Calendar.YEAR, -1)
        year = calender.get(Calendar.YEAR)
        month = calender.get(Calendar.MONTH) + 1
        day = calender.get(Calendar.DAY_OF_MONTH)
        DisplayDate = String.format("%04d", year) + "." + String.format("%02d", month) + "." + String.format("%02d", day)
    }

    val YearPlusOne = {
        var year = DisplayDate.slice(0..3).toInt()
        var month = DisplayDate.slice(5..6).toInt()
        var day = DisplayDate.slice(8..9).toInt()
        val calender = Calendar.getInstance()
        calender.set(year, month-1, day)
        calender.add(Calendar.YEAR, 1)
        year = calender.get(Calendar.YEAR)
        month = calender.get(Calendar.MONTH) + 1
        day = calender.get(Calendar.DAY_OF_MONTH)
        DisplayDate = String.format("%04d", year) + "." + String.format("%02d", month) + "." + String.format("%02d", day)
    }

    val ImageChange = when (CalenderImage) {
        CalenderPage.Days -> {
            { CalenderImage = CalenderPage.Months }
        }
        CalenderPage.Months -> {
            { CalenderImage = CalenderPage.Years }
        }
        CalenderPage.Years -> {
            { CalenderImage = CalenderPage.Days }
        }
    }

    when (CalenderImage) {
        CalenderPage.Days -> CalenderDays(
            DisplayDate = DisplayDate,
            CurrentDate = CurrentDate,
            width = width,
            height = height,
            MonthMinusOne = MonthMinusOne,
            MonthPlusOne = MonthPlusOne,
            CurrentDateUpdate = CurrentDateUpdate,
            color = color,
            ImageChange = ImageChange
        )
        CalenderPage.Months -> CalenderMonths(
            DisplayDate = DisplayDate,
            width = width,
            height = height,
            YearMinusOne = YearMinusOne,
            YearPlusOne = YearPlusOne,
            ChangeMonth = ChangeMonth,
            ImageChange = ImageChange
        )
        CalenderPage.Years -> CalenderYears(
            DisplayDate = DisplayDate,
            width = width,
            height = height,
            ChangeYear = ChangeYear,
            ImageChange = ImageChange
        )
    }
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
                 MonthMinusOne: () -> Unit, MonthPlusOne: () -> Unit, CurrentDateUpdate: (String) -> Unit, ImageChange:() -> Unit) {
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
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .background(color = Color.White)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height((height*40/304).dp)
        ) {
            Box(modifier = Modifier.width((width*35/349).dp)) {}
            Box(
                modifier = Modifier
                    .width((width * 20 / 349).dp)
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
                    modifier = Modifier.align(Alignment.Center).clickable(onClick = ImageChange),
                    text = DisplayDate.slice(0..6),
                    fontSize = (height*20/304).sp,
                )
            }
            Box(modifier = Modifier
                .width((width * 20 / 349).dp)
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
                modifier = Modifier
                    .height((height * 3 / 304).dp)
                    .width((width * 300 / 349).dp)
                    .background(color = Color(0xFFD9D9D9))
            )
        }
        Column(
            modifier = Modifier
                .height((height * 230 / 304).dp)
                .width((width * 330 / 349).dp)
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
                                        modifier = Modifier
                                            .size((height * 205 / 304 / NumOfWeek * 0.9).dp)
                                            .clip(CircleShape)
                                            .background(color = if ((year == curyear) and (month == curmonth) and (i + 1 - FirstDoW == curday)) color else Color.White)
                                            .clickable() {
                                                CurrentDateUpdate(
                                                    String.format(
                                                        "%04d",
                                                        year
                                                    ) + "." + String.format(
                                                        "%02d",
                                                        month
                                                    ) + "." + String.format(
                                                        "%02d",
                                                        i + 1 - FirstDoW
                                                    )
                                                )
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
                                        modifier = Modifier
                                            .size((height * 205 / 304 / NumOfWeek * 0.9).dp)
                                            .clip(CircleShape)
                                            .background(color = if ((year == curyear) and (month == curmonth) and (day + i == curday)) color else Color.White)
                                            .clickable() {
                                                CurrentDateUpdate(
                                                    String.format(
                                                        "%04d",
                                                        year
                                                    ) + "." + String.format(
                                                        "%02d",
                                                        month
                                                    ) + "." + String.format("%02d", day + i)
                                                )
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
                                        modifier = Modifier
                                            .size((height * 205 / 304 / NumOfWeek * 0.9).dp)
                                            .clip(CircleShape)
                                            .background(color = if ((year == curyear) and (month == curmonth) and (day + i == curday)) color else Color.White)
                                            .clickable() {
                                                CurrentDateUpdate(
                                                    String.format(
                                                        "%04d",
                                                        year
                                                    ) + "." + String.format(
                                                        "%02d",
                                                        month
                                                    ) + "." + String.format("%02d", day + i)
                                                )
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
fun CalenderMonths(DisplayDate: String, width: Int, height: Int, modifier: Modifier = Modifier,
                   YearMinusOne: () -> Unit, YearPlusOne: () -> Unit, ChangeMonth: (Int) -> Unit, ImageChange:() -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .background(color = Color.White)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height((height*40/304).dp)
        ) {
            Box(modifier = Modifier.width((width*35/349).dp)) {}
            Box(
                modifier = Modifier
                    .width((width * 20 / 349).dp)
                    .clickable(onClick = YearMinusOne)
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
                    modifier = Modifier.align(Alignment.Center).clickable(onClick = ImageChange),
                    text = DisplayDate.slice(0..3) + "년",
                    fontSize = (height*20/304).sp,
                )
            }
            Box(modifier = Modifier
                .width((width * 20 / 349).dp)
                .clickable(onClick = YearPlusOne)) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = ">",
                    fontSize = (height*20/304).sp
                )
            }
            Box(modifier = Modifier.width((width*35/349).dp))
        }
        Box(
            modifier = Modifier.height((height*8/304).dp)
        ) {
            Box(
                modifier = Modifier
                    .height((height * 3 / 304).dp)
                    .width((width * 300 / 349).dp)
                    .background(color = Color(0xFFD9D9D9))
            )
        }
        Column(
            modifier = Modifier
                .height((height * 230 / 304).dp)
                .width((width * 330 / 349).dp)
                .background(color = Color.White)
        ) {
            //달력 내부 ToDO
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.height((height*10/304).dp))
                Row(modifier = Modifier.weight(0.33f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(1) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "1월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                    Box(modifier = Modifier.width((width*10/349).dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(2) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "2월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                    Box(modifier = Modifier.width((width*10/349).dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(3) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "3월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                    Box(modifier = Modifier.width((width*10/349).dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(4) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "4월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                }
                Box(modifier = Modifier.height((height*10/304).dp))
                Row(modifier = Modifier.weight(0.33f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(5) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "5월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                    Box(modifier = Modifier.width((width*10/349).dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(6) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "6월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                    Box(modifier = Modifier.width((width*10/349).dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(7) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "7월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                    Box(modifier = Modifier.width((width*10/349).dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(8) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "8월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                }
                Box(modifier = Modifier.height((height*10/304).dp))
                Row(modifier = Modifier.weight(0.33f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(9) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "9월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                    Box(modifier = Modifier.width((width*10/349).dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(10) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "10월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                    Box(modifier = Modifier.width((width*10/349).dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(11) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "11월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                    Box(modifier = Modifier.width((width*10/349).dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(0.25f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(15.dp))
                            .clickable(onClick = { ChangeMonth(12) })
                    ) {
                        Box(modifier = Modifier.weight(0.5f))
                        Text(
                            text = "12월",
                            fontSize = (height * 15/304).sp
                        )
                        Box(modifier = Modifier.weight(0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun CalenderYears(DisplayDate: String, width: Int, height: Int, modifier: Modifier = Modifier,
                  ChangeYear: (Int) -> Unit, ImageChange:() -> Unit) {
    var text by remember { mutableStateOf(DisplayDate.slice(0..3)) }

    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .background(color = Color.White)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height((height*40/304).dp)
        ) {
            Box(modifier = Modifier.weight(0.5f))
            Box(
                modifier = Modifier.width((width*239/349).dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center).clickable(onClick = ImageChange),
                    text = "연도를 입력하세요",
                    fontSize = (height*20/304).sp,
                )
            }
            Box(modifier = Modifier.weight(0.5f))
        }
        Box(
            modifier = Modifier.height((height*8/304).dp)
        ) {
            Box(
                modifier = Modifier
                    .height((height * 3 / 304).dp)
                    .width((width * 300 / 349).dp)
                    .background(color = Color(0xFFD9D9D9))
            )
        }
        Column(
            modifier = Modifier
                .height((height * 230 / 304).dp)
                .width((width * 330 / 349).dp)
                .background(color = Color.White)
        ) {
            //달력 내부 ToDO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.weight(0.5f))
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.width((width*100/349).dp)) {
                        BasicTextField(
                            value = text,
                            onValueChange = { newText ->
                                text = newText
                            },
                            modifier = Modifier.width((width*200/349).dp),
                            textStyle = TextStyle(
                                fontSize = (height * 40 / 304).sp,
                                textAlign = TextAlign.End
                            ),
                        )
                    }
                    Box(modifier = Modifier.width((width*10/349).dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1294F2)),
                        onClick = {
                            if (text.length > 4) text = text.slice(0..3)
                            val year = text.toIntOrNull()
                            if (year != null) ChangeYear(year)
                            else Toast.makeText(context, "올바른 연도를 입력해주세요.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.width((width*100/349).dp).height((height*40/304).dp),
                    ) {
                        Text(
                            text = "입력",
                            fontSize = (height*20/304).sp
                        )
                    }
                }
                Box(modifier = Modifier.weight(0.5f))
            }
        }
    }
}


@Preview
@Composable
fun previewMain() {
    CalenderMain(width = 349, height = 304, color = Color(0xFF1294F2))
}