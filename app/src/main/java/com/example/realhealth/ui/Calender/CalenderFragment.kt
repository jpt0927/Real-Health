package com.example.realhealth.ui.Calender

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.example.realhealth.R
import com.example.realhealth.model.todo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

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

fun saveTodosToJson(context: Context, todos: List<todo>, fileName: String = "todos.json") {
    val json = Json { prettyPrint = true }

    try {
        val jsonString = json.encodeToString(todos)

        val file = File(context.filesDir, fileName)

        file.writeText(jsonString)
        println("JSON 저장 성공")
    } catch (e: Exception) {
        e.printStackTrace()
        println("JSON 저장 실패")
    }
}

fun loadTodos(context: Context, fileName: String = "todos.json"): List<todo> {
    try {
        val file = File(context.filesDir, fileName)

        if (!file.exists()) {
            println("파일 없다 티비~")
            return emptyList()
        }

        val jsonString = file.readText()

        val todos = Json.decodeFromString<List<todo>>(jsonString)
        println("불러왔다티비!!")
        return todos
    } catch (e: Exception) {
        e.printStackTrace()
        println("JSON 불러오기 실패")
        return emptyList()
    }
}

@Composable
fun MainAppCalender() {
    val layoutDirection = LocalLayoutDirection.current

    var AddingScreenOn by remember { mutableStateOf(false) }

    val calender = java.util.Calendar.getInstance()
    var currentDate by remember { mutableStateOf(String.format("%04d", calender.get(Calendar.YEAR)) + "." + String.format("%02d", calender.get(Calendar.MONTH) + 1) + "." + String.format("%02d", calender.get(Calendar.DAY_OF_MONTH))) }

    val currentDateUpdate = { NewDate: String ->
        currentDate = NewDate
    }

    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
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
            Box(
                modifier = Modifier.shadow(elevation = 10.dp, shape = RoundedCornerShape(17.dp))
                    .clip(RoundedCornerShape(17.dp))
            ) {
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
        MainAddingTodos(state = AddingScreenOn, onClick = { AddingScreenOn = !AddingScreenOn }, Updater = { })
        AddTodosButton(state = AddingScreenOn, onClick = { AddingScreenOn = !AddingScreenOn })
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

@Composable
fun AddTodosButton(state: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = !state,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight }
        ),
        exit = slideOutVertically (
            targetOffsetY  = { fullHeight -> fullHeight }
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(modifier = Modifier
                .clip(CircleShape)
                .background(color = Color(0xFF2196F3))
                .size(55.dp)
                .clickable() { onClick() }
            ) {
                Text(
                    text = "+",
                    color = Color(0xFFEEEEEE),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Box(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun CategoryToolBar(currentCategory: String, state: Boolean, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    AnimatedVisibility(
        visible = state,
        enter = slideInVertically { fullHeight -> -fullHeight } + expandIn(expandFrom = Alignment.TopCenter) + fadeIn(),
        exit = slideOutVertically { fullHeight -> -fullHeight } + shrinkOut(shrinkTowards = Alignment.TopCenter) + fadeOut()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn {
                items(
                    listOf(
                        "얼굴 운동",
                        "승모근 운동",
                        "등 운동",
                        "가슴 운동",
                        "복근 운동",
                        "하체 운동",
                        "팔다리 운동"
                    )
                ) { item ->
                    Box(
                        modifier = Modifier
                            .shadow(3.dp, shape = RoundedCornerShape(8.dp))
                            .height(34.dp)
                            .width(347.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFAEDBEE))
                            .clickable() {
                                onClick(item)
                            }
                    ) {
                        Text(
                            text = " $item",
                            fontSize = 20.sp,
                            fontWeight = if (currentCategory == item) FontWeight.Bold else FontWeight.Light,
                            textAlign = TextAlign.Left,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainAddingTodos(state: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit, Updater: () -> Unit) {
    val calender = java.util.Calendar.getInstance()
    var currentDate by remember { mutableStateOf(String.format("%04d", calender.get(Calendar.YEAR)) + "." + String.format("%02d", calender.get(Calendar.MONTH) + 1) + "." + String.format("%02d", calender.get(Calendar.DAY_OF_MONTH))) }

    val focusManager = LocalFocusManager.current

    var textboxTask by remember { mutableStateOf("") }
    var textboxCategory by remember { mutableStateOf("얼굴 운동") }
    var textboxNotes by remember { mutableStateOf("") }

    var ShowCategories by remember { mutableStateOf(false) }

    val CategoryUpdate = { NewCategory: String ->
        textboxCategory = NewCategory
        ShowCategories = !ShowCategories
    }

    val currentDateUpdate = { NewDate: String ->
        currentDate = NewDate
    }

    var offsetY by remember { mutableStateOf(0f) }
    val density = LocalDensity.current.density

    var buttonEnable by remember { mutableStateOf(true) }

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var savedImagePath by remember { mutableStateOf<String?>(null) }

    var weightedCalenderHeightdp by remember { mutableStateOf(0) }
    var weightedCalenderWidthdp by remember { mutableStateOf(0) }

    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state) offsetY = 0f
        selectedImageUris = emptyList()
    }

    AnimatedVisibility(
        visible = state,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight }
        ),
        exit = slideOutVertically (
            targetOffsetY  = { fullHeight -> fullHeight }
        )
    ) {
        Column(
            modifier = Modifier
                .offset { IntOffset(0, offsetY.roundToInt()) }
                .clickable(enabled = false) {},
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Box(modifier = Modifier
                .height(17.dp)
                .width(370.dp)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        if ((offsetY == 0f) and (delta > 0)) {
                            offsetY += delta
                        } else if (offsetY > 0) {
                            offsetY += delta
                        }
                        if (offsetY < 0) offsetY = 0f
                    },
                    onDragStarted = {
                        buttonEnable = false
                    },
                    onDragStopped = { velocity ->
                        buttonEnable = true
                        if ((velocity > 9000f) or (offsetY / density > 270.dp.value)) {
                            onClick()
                        } else {
                            offsetY = 0f
                        }
                    }
                )
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
                    .width(370.dp)
                    .height(705.dp)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier = Modifier
                            .height(18.dp)
                            .draggable(
                                orientation = Orientation.Vertical,
                                state = rememberDraggableState { delta ->
                                    if ((offsetY == 0f) and (delta > 0)) {
                                        offsetY += delta
                                    } else if (offsetY > 0) {
                                        offsetY += delta
                                    }
                                    if (offsetY < 0) offsetY = 0f
                                },
                                onDragStarted = {
                                    buttonEnable = false
                                },
                                onDragStopped = { velocity ->
                                    buttonEnable = true
                                    if ((velocity > 9000f) or (offsetY / density > 270.dp.value)) {
                                        onClick()
                                    } else {
                                        offsetY = 0f
                                    }
                                }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .height(5.dp)
                                .width(370.dp)
                        )
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(4.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF9D9D9D))
                                    .padding(100.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(9.dp)
                                .width(370.dp)
                        )
                    }
                    // 여기서부터 만들면 된다 튀뷔
                    Row() {
                        Box(modifier = Modifier.width(10.dp))
                        Text(
                            fontSize = 22.sp,
                            color = Color(0xFF2196F3),
                            fontWeight = FontWeight.SemiBold,
                            text = "Add Task",
                        )
                        Box(modifier = Modifier.weight(1f))
                    }
                    Box(modifier = Modifier.height(5.dp))
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .height(34.dp)
                            .width(347.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFB0D2E0))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = " ",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Left
                            )
                            BasicTextField(
                                value = textboxTask,
                                onValueChange = { textboxTask = it },
                                textStyle = TextStyle(
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Left
                                ),
                                singleLine = true,
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                    }
                                ),
                                decorationBox = { innerTextField ->
                                    Box() {
                                        if (textboxTask.isEmpty()) {
                                            Text(
                                                text = "이곳에 할 일을 적습니다.",
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Left,
                                                color = Color(0xFF949494),
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                                modifier = Modifier
                                    .width(347.dp)
                            )
                        }
                    }
                    Box(modifier = Modifier.height(10.dp))
                    Row() {
                        Box(modifier = Modifier.width(10.dp))
                        Text(
                            fontSize = 22.sp,
                            color = Color(0xFF2196F3),
                            fontWeight = FontWeight.SemiBold,
                            text = "Category"
                        )
                        Box(modifier = Modifier.weight(1f))
                    }
                    Box(modifier = Modifier.height(5.dp))
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .height(34.dp)
                            .width(347.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFB0D2E0))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = " ",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Left
                            )
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = textboxCategory,
                                    fontSize = 20.sp,
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )
                            }
                            Box(
                                modifier = Modifier.width(30.dp).height(30.dp)
                                    .clickable() {
                                        ShowCategories = !ShowCategories
                                    }
                            ) {
                                Text(
                                    text = " ↓ ",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(modifier = Modifier.height(10.dp))
                            Row() {
                                Box(modifier = Modifier.width(10.dp))
                                Text(
                                    fontSize = 22.sp,
                                    color = Color(0xFF2196F3),
                                    fontWeight = FontWeight.SemiBold,
                                    text = "Notes"
                                )
                                Box(modifier = Modifier.weight(1f))
                            }
                            Box(modifier = Modifier.height(5.dp))
                            Box() {
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        contentAlignment = Alignment.CenterStart,
                                        modifier = Modifier
                                            .weight(1f)
                                            .width(347.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFB0D2E0))
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = " ",
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Left
                                            )
                                            BasicTextField(
                                                value = textboxNotes,
                                                onValueChange = { textboxNotes = it },
                                                textStyle = TextStyle(
                                                    fontSize = 20.sp,
                                                    textAlign = TextAlign.Left
                                                ),
                                                singleLine = false,
                                                keyboardActions = KeyboardActions(
                                                    onDone = {
                                                        focusManager.clearFocus()
                                                    }
                                                ),
                                                decorationBox = { innerTextField ->
                                                    Box() {
                                                        if (textboxNotes.isEmpty()) {
                                                            Text(
                                                                text = "무엇을 해야 하는지 기록합니다.",
                                                                fontSize = 20.sp,
                                                                textAlign = TextAlign.Left,
                                                                color = Color(0xFF949494),
                                                            )
                                                        }
                                                        innerTextField()
                                                    }
                                                },
                                                modifier = Modifier
                                                    .width(347.dp)
                                                    .fillMaxHeight()
                                            )
                                        }
                                    }
                                    Box(modifier = Modifier.height(10.dp))
                                    Row() {
                                        Button(
                                            onClick = {
                                                if (selectedImageUris.size > 0) {
                                                    val filename =
                                                        "MYIMG_${currentDate.slice(0..3)}${
                                                            currentDate.slice(
                                                                5..6
                                                            )
                                                        }${
                                                            currentDate.slice(8..9)
                                                        }"

                                                    for (i in 0..selectedImageUris.size - 1) {
                                                        val file = File(
                                                            context.filesDir,
                                                            filename + i.toString() + ".jpg"
                                                        )
                                                        try {
                                                            val outputStream =
                                                                FileOutputStream(file)
                                                            val inputStream =
                                                                context.contentResolver.openInputStream(
                                                                    selectedImageUris[i]
                                                                )
                                                            inputStream.use { input ->
                                                                outputStream.use { output ->
                                                                    input?.copyTo(output)
                                                                }
                                                            }
                                                            Toast.makeText(
                                                                context,
                                                                "${selectedImageUris.size}개의 파일을 저장하였습니다.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } catch (e: Exception) {
                                                            Toast.makeText(
                                                                context,
                                                                "파일을 저장하지 못했습니다.",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                                    selectedImageUris = emptyList()
                                                    Updater()
                                                    onClick()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "선택된 파일이 없습니다.",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                }
                                            },
                                            modifier = Modifier
                                                .width(161.dp)
                                                .height(34.44.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFB0E0C1),
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("등록")
                                        }
                                        Box(modifier = Modifier.width(25.dp))
                                        Button(
                                            onClick = {
                                                selectedImageUris = emptyList()
                                                onClick()
                                            },
                                            modifier = Modifier
                                                .width(161.dp)
                                                .height(34.44.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFE0B0B1),
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("취소")
                                        }
                                    }
                                    Box(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                        CategoryToolBar(
                            currentCategory = textboxCategory,
                            state = (ShowCategories && state),
                            onClick = CategoryUpdate,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}



@Preview
@Composable
fun preview() {
    Tab3TodoList(listOf("하암", "피곤", "하다티비", "아오\n 졸려"))
}