package com.example.realhealth.ui.Calender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.realhealth.databinding.FragmentCalenderBinding
import com.example.realhealth.ui.Calender.CalenderViewModel
import com.example.realhealth.ui.Gallery.MainApp
import com.example.realhealth.ui.ShowCalender.CalenderMain
import java.util.Calendar
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items

class CalenderFragment : Fragment() {

    private var _binding: FragmentCalenderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(CalenderViewModel::class.java)

        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.composeViewCalender.setContent {
            MainAppCalender()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun loadTodos() {

}

@Composable
fun MainAppCalender() {
    val layoutDirection = LocalLayoutDirection.current

    val calender = java.util.Calendar.getInstance()
    var currentDate by remember { mutableStateOf(String.format("%04d", calender.get(Calendar.YEAR)) + "." + String.format("%02d", calender.get(Calendar.MONTH) + 1) + "." + String.format("%02d", calender.get(Calendar.DAY_OF_MONTH))) }

    val currentDateUpdate = { NewDate: String ->
        currentDate = NewDate
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .statusBarsPadding()
            .padding(
                start = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateStartPadding(layoutDirection),
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(layoutDirection),
                bottom = 56.dp
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(17.dp))) {
                CalenderMain(
                    currentDate = currentDate,
                    currentDateUpdate = currentDateUpdate,
                    348,
                    302,
                    Color(0xFF1294F2)
                )
            }
            Box(modifier = Modifier.height(20.dp))
            Tab3TodoList(listOf("temp", "temp2", "temp3", "temp4", "temp5", "temp6", "temp7", "temp8", "temp9", "temp10"), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun Tab3TodoList(ContentList: List<String>, modifier: Modifier = Modifier) {
    LazyColumn () {
        items(ContentList) { item ->
            Tab3Todo(item)
            Box(modifier = Modifier.height(7.dp))
        }
    }
}

@Composable
fun Tab3Todo(textcontent: String, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.width(330.dp).height(129.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color(0x262196F3))
    ) {
        Column() {
            Box(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Box(modifier = Modifier.width(10.dp))
                Text(
                    fontSize = 20.sp,
                    color = Color(0xFF2196F3),
                    text = "16/09/2023",
                    fontWeight = FontWeight.Bold
                )
                Box(modifier = Modifier.weight(1f))
                Text(
                    fontSize = 14.sp,
                    color = Color(0xFFFF5722),
                    text = "Delete",
                    modifier = Modifier.clickable() {
                        println("I want to delete.")
                    }
                )
                Box(modifier = Modifier.width(25.dp))
            }
            Box(modifier = Modifier.height(5.dp))
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Row() {
                    Box(modifier = Modifier.width(10.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        color = Color(0xFF333333),
                        fontWeight = FontWeight.Light,
                        text = textcontent
                    )
                    Box(modifier = Modifier.width(10.dp))
                }
            }
            Box(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview
@Composable
fun preview() {
    Tab3TodoList(listOf("하암", "피곤", "하다티비", "아오\n 졸려"))
}