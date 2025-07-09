package com.example.realhealth.ui.Calender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.ui.Alignment
import com.example.realhealth.R
import com.example.realhealth.databinding.FragmentCalenderBinding
import com.example.realhealth.model.Exercise
import com.example.realhealth.model.todo
import com.example.realhealth.ui.ShowCalender.CalenderMain
import com.google.gson.Gson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Calendar
import kotlin.math.roundToInt
import androidx.compose.material3.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

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

@Composable
fun MainAppCalender() {
    val layoutDirection = LocalLayoutDirection.current

    var AddingScreenOn by remember { mutableStateOf(false) }

    val calender = java.util.Calendar.getInstance()
    var currentDate by remember {
        mutableStateOf(
            String.format("%04d", calender.get(Calendar.YEAR)) + "." +
                    String.format("%02d", calender.get(Calendar.MONTH) + 1) + "." +
                    String.format("%02d", calender.get(Calendar.DAY_OF_MONTH))
        )
    }

    val todayDate = String.format("%04d", calender.get(Calendar.YEAR)) + "." +
            String.format("%02d", calender.get(Calendar.MONTH) + 1) + "." +
            String.format("%02d", calender.get(Calendar.DAY_OF_MONTH))

    val currentDateUpdate = { NewDate: String ->
        currentDate = NewDate
    }

    var jsonDataList: List<todo> by remember { mutableStateOf(emptyList()) }

    // 운동 추천 팝업 상태
    /*val showSuggestionDialog = remember { mutableStateOf(false) }*/
    val apiKey = "" // 👉 여긴 안전하게 보관하세요

    var showSuggestionDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val filename = "TodoData.json"
    val file = File(context.filesDir, filename)

    if (file.exists()) {
        val jsonStringDataList = file.readText()
        jsonDataList = Gson().fromJson(jsonStringDataList, Array<todo>::class.java).toList()
    } else {
        jsonDataList = emptyList()
    }

    // 운동 기록이 없을 때만 버튼 노출
    var hasNoWorkout by remember { mutableStateOf(jsonDataList.none { it.date == todayDate }) }

    val UpdateDataList = {
        if (file.exists()) {
            val jsonStringDataList = file.readText()
            jsonDataList = Gson().fromJson(jsonStringDataList, Array<todo>::class.java).toList()
        } else {
            jsonDataList = emptyList()
        }
    }

    val todoDelete = { data: todo ->
        jsonDataList = jsonDataList.filterNot { (it.name == data.name) && (it.date == data.date) }
        val jsonStringDataList = Json.encodeToString(jsonDataList)
        file.writeText(jsonStringDataList)
        UpdateDataList()
        Toast.makeText(context, "운동을 삭제하였습니다.", Toast.LENGTH_SHORT).show()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(
                start = WindowInsets.safeDrawing.asPaddingValues().calculateStartPadding(layoutDirection),
                end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(layoutDirection),
                bottom = 56.dp
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            Box(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(17.dp))
                    .clip(RoundedCornerShape(17.dp))
            ) {
                CalenderMain(
                    currentDate = currentDate,
                    currentDateUpdate = currentDateUpdate,
                    348,
                    302,
                    Color(0xFF1565C0)
                )
            }
            Box(modifier = Modifier.height(20.dp))
            Tab3TodoList(
                currentDate = currentDate,
                ContentList = jsonDataList,
                modifier = Modifier.weight(1f),
                delete = todoDelete
            )
        }
        AnimatedVisibility(
            visible = hasNoWorkout && (currentDate == todayDate),
            enter = slideInHorizontally(
                initialOffsetX = { fullHeight -> fullHeight }
            ),
            exit = slideOutHorizontally (
                targetOffsetX = { fullHeight -> fullHeight }
            ),
            modifier = Modifier.size(50.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.padding(10.dp)
            ) {
                Box(
                    modifier = Modifier.size(50.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            showSuggestionDialog = true
                            hasNoWorkout = false
                        },
                        containerColor = Color(0xFFDCEFFF)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "추천 운동",
                            tint = Color(0xFF1565C0)
                        )
                    }
                }
            }
        }

        if (showSuggestionDialog) {
            WorkoutSuggestionDialog(
                onDismiss = { showSuggestionDialog = false },
                apiKey = apiKey
            )
        }

        MainAddingTodos(
            currentdate = currentDate,
            state = AddingScreenOn,
            onClick = { AddingScreenOn = !AddingScreenOn },
            Updater = UpdateDataList
        )
        AddTodosButton(
            state = AddingScreenOn,
            onClick = { AddingScreenOn = !AddingScreenOn }
        )
    }
}

@Composable
fun WorkoutSuggestionDialog(
    onDismiss: () -> Unit,
    apiKey: String
) {
    var suggestion by remember { mutableStateOf("로딩 중...") }
    val coroutineScope = rememberCoroutineScope()

    // GPT API 요청
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            suggestion = getWorkoutSuggestion(apiKey)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFB0D2E0),
        title = { Text("오늘의 운동 추천") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp), // 높이 조정하여 중간 정렬 가능하게
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.realhealh_c),
                        contentDescription = "운동 이미지",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = suggestion,
                        textAlign = TextAlign.Center,
                        color = Color.White // 글자색도 변경 가능
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기", color = Color.White)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

suspend fun getWorkoutSuggestion(apiKey: String): String {
    val client = OkHttpClient()

    val json = """
        {
          "model": "gpt-3.5-turbo",
          "messages": [{"role": "user", "content": "오늘 할 헬스 부위 하나를 추천해주고 그에 맞는 운동들과 같이 간단한 이유를 세 문장정도만 설명해줘"}]
        }
    """.trimIndent()

    val request = Request.Builder()
        .url("https://api.openai.com/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey")
        .addHeader("Content-Type", "application/json")
        .post(RequestBody.create("application/json".toMediaTypeOrNull(), json))
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            val body = response.body?.string()
            val jsonObject = JSONObject(body ?: "")
            val content = jsonObject
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
            content.trim()
        } catch (e: Exception) {
            e.printStackTrace()
            "추천을 불러오는 데 실패했어요."
        }
    }
}

@Composable
fun Tab3TodoList(currentDate: String, ContentList: List<todo>, modifier: Modifier = Modifier, delete: (todo) -> Unit) {
    Box() {
        LazyColumn() {
            items(ContentList) { item ->
                if (currentDate == item.date) {
                    Tab3Todo(item, delete = delete)
                    Box(modifier = Modifier.height(7.dp))
                }
            }
        }
        if (ContentList.filter {it.date == currentDate}.size == 0) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(modifier = Modifier.weight(0.5f))
                Text(
                    text = "해당 날짜에 운동이 존재하지 않습니다.",
                    fontSize = 16.sp,
                )
                Box(modifier = Modifier.weight(0.5f))
            }
        }
    }
}

@Composable
fun Tab3Todo(textcontent: todo, modifier: Modifier = Modifier, delete: (todo) -> Unit) {
    Box(
        modifier = Modifier
            .width(330.dp)
            .height(129.dp)
            .shadow(3.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color(0xFFE9F5FE))
    ) {
        Column() {
            Box(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Box(modifier = Modifier.width(10.dp))
                Text(
                    fontSize = 20.sp,
                    color = Color(0xFF000000),
                    text = textcontent.date,
                    fontWeight = FontWeight.Bold
                )
                Box(modifier = Modifier.weight(1f))
                Text(
                    fontSize = 14.sp,
                    color = Color(0xFFFF5722),
                    text = "Delete",
                    modifier = Modifier.clickable() {
                        delete(textcontent)
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
                    Box(modifier = Modifier.width(10.dp))
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                            color = Color(0xFF333333),
                            fontWeight = FontWeight.Bold,
                            text = textcontent.name
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                            color = Color(0xFF333333),
                            fontWeight = FontWeight.Light,
                            text = textcontent.content
                        )
                    }
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
                .background(color = Color(0xFF1565C0))
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
fun CategoryToolBar(currentCategory: String, list: List<String>, state: Boolean, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
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
            LazyColumn (
                modifier = Modifier.height(100.dp)
            ){
                items(list) { item ->
                    Box(
                        modifier = Modifier
                            .shadow(3.dp, shape = RoundedCornerShape(8.dp))
                            .height(34.dp)
                            .width(347.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F8FF))
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
fun EquipToolBar(currentEquip: String, list: List<String>, state: Boolean, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
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
            LazyColumn (
                modifier = Modifier.height(100.dp)
            ){
                items(list) { item ->
                    Box(
                        modifier = Modifier
                            .shadow(3.dp, shape = RoundedCornerShape(8.dp))
                            .height(34.dp)
                            .width(347.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F8FF))
                            .clickable() {
                                onClick(item)
                            }
                    ) {
                        Text(
                            text = " $item",
                            fontSize = 20.sp,
                            fontWeight = if (currentEquip == item) FontWeight.Bold else FontWeight.Light,
                            textAlign = TextAlign.Left,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainAddingTodos(currentdate: String, state: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit, Updater: () -> Unit) {
    val calender = java.util.Calendar.getInstance()
    var currentDate by remember { mutableStateOf(String.format("%04d", calender.get(Calendar.YEAR)) + "." + String.format("%02d", calender.get(Calendar.MONTH) + 1) + "." + String.format("%02d", calender.get(Calendar.DAY_OF_MONTH))) }

    if (currentdate != "") currentDate = currentdate

    val focusManager = LocalFocusManager.current

    var textboxEquip by remember { mutableStateOf("") }
    var textboxCategory by remember { mutableStateOf("") }
    var textboxExercise by remember { mutableStateOf("") }
    var textboxNotes by remember { mutableStateOf("") }

    var ShowEquips by remember { mutableStateOf(false) }
    var ShowCategories by remember { mutableStateOf(false) }
    var ShowExercises by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    val CategoryUpdate = { NewCategory: String ->
        if (textboxCategory != NewCategory) {
            textboxCategory = NewCategory
            textboxExercise = ""
        }
        ShowCategories = !ShowCategories
    }

    val ExercisesUpdate = { NewExercise: String ->
        if (textboxExercise != NewExercise) {
            textboxExercise = NewExercise
        }
        ShowExercises = !ShowExercises
    }

    val EquipsUpdate = { NewEquip: String ->
        if (textboxEquip != NewEquip) {
            textboxEquip = NewEquip
            textboxExercise = ""
        }

        ShowEquips = !ShowEquips
    }

    var offsetY by remember { mutableStateOf(0f) }
    val density = LocalDensity.current.density

    var buttonEnable by remember { mutableStateOf(true) }

    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state) offsetY = 0f
        textboxEquip = ""
        textboxCategory = ""
        textboxExercise = ""
        textboxNotes = ""
        ShowEquips = false
        ShowCategories = false
        ShowExercises = false
    }

    val view = LocalView.current
    val navController: NavController = remember(view) {
        try {
            Navigation.findNavController(view)
        } catch (e: Exception) {
            throw IllegalStateException("NavController not found")
        }
    }

    BackHandler {
        if (ShowEquips) ShowEquips = false
        else if (ShowCategories) ShowCategories = false
        else if (ShowExercises) ShowExercises = false
        else if (state) navController.navigate(R.id.navigation_calender)
        else navController.navigate(R.id.navigation_home)
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
                    .background(Color(0xFFECEFF1)),
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
                    Text(
                        fontSize = 30.sp,
                        color = Color(0xFF000000),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        text = currentDate,
                    )
                    Box(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier.height(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(347.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(color = Color(0xFFCCCCCC))
                        )
                    }
                    Box(modifier = Modifier.height(15.dp))
                    Row() {
                        Box(modifier = Modifier.width(10.dp))
                        Text(
                            fontSize = 22.sp,
                            color = Color(0xFF000000),
                            fontWeight = FontWeight.SemiBold,
                            text = "기구/장비",
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
                            .background(Color(0xFFF0F8FF))
                            .clickable() {
                                if (buttonEnable) {
                                    ShowEquips = !ShowEquips
                                    ShowExercises = false
                                    ShowCategories = false
                                }
                            }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
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
                                    text = textboxEquip,
                                    fontSize = 20.sp,
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(30.dp)
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
                                    color = Color(0xFF000000),
                                    fontWeight = FontWeight.SemiBold,
                                    text = "부위"
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
                                    .background(Color(0xFFF0F8FF))
                                    .clickable() {
                                        if (buttonEnable) {
                                            ShowCategories = !ShowCategories
                                            ShowExercises = false
                                            ShowEquips = false
                                        }
                                    }
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
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
                                        modifier = Modifier
                                            .width(30.dp)
                                            .height(30.dp)
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
                                            color = Color(0xFF000000),
                                            fontWeight = FontWeight.SemiBold,
                                            text = "운동",
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
                                            .background(Color(0xFFF0F8FF))
                                            .clickable() {
                                                if (buttonEnable) {
                                                    ShowExercises = !ShowExercises
                                                    ShowEquips = false
                                                    ShowCategories = false
                                                    if (ShowExercises) focusRequester.requestFocus()
                                                    else focusManager.clearFocus()
                                                }
                                            }
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                        ) {
                                            Text(
                                                text = " ",
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Left,
                                            )
                                            Box(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                BasicTextField(
                                                    value = textboxExercise,
                                                    onValueChange = { textboxExercise = it },
                                                    textStyle = TextStyle(
                                                        fontSize = 20.sp,
                                                        textAlign = TextAlign.Left,
                                                        color = Color(0xFFeceff1),
                                                    ),
                                                    decorationBox = { innerTextField ->
                                                        Box() {
                                                            if (textboxExercise.isEmpty()) {
                                                                Text(
                                                                    text = "",
                                                                    fontSize = 20.sp,
                                                                    textAlign = TextAlign.Left,
                                                                    color = Color(0xFFeceff1),
                                                                )
                                                            }
                                                            innerTextField()
                                                        }
                                                    },
                                                    singleLine = true,
                                                    keyboardActions = KeyboardActions(
                                                        onDone = {
                                                            focusManager.clearFocus()
                                                        }
                                                    ),
                                                    modifier = Modifier
                                                        .align(Alignment.CenterStart)
                                                        .focusRequester(focusRequester)
                                                )
                                                Text(
                                                    text = textboxExercise,
                                                    fontSize = 20.sp,
                                                    modifier = Modifier.align(Alignment.CenterStart)
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .width(30.dp)
                                                    .height(30.dp)
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
                                                    color = Color(0xFF000000),
                                                    fontWeight = FontWeight.SemiBold,
                                                    text = "노트"
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
                                                            .background(Color(0xFFF0F8FF))
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
                                                                                textAlign = TextAlign.Center,
                                                                                color = Color(0xFF37474F),
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
                                                                if (textboxExercise.length > 0) {
                                                                    val filename = "TodoData.json"
                                                                    val file = File(context.filesDir, filename)

                                                                    if (file.exists()) {
                                                                        val jsonStringDataList =
                                                                            file.readText()
                                                                        val jsonDataList =
                                                                            Gson().fromJson(
                                                                                jsonStringDataList,
                                                                                Array<todo>::class.java
                                                                            ).toList()
                                                                        val data = todo(
                                                                            currentDate,
                                                                            textboxExercise,
                                                                            textboxNotes
                                                                        )

                                                                        val dupData = jsonDataList.find { it.date == data.date && it.name == data.name }
                                                                        if (dupData != null) {
                                                                            Toast.makeText(
                                                                                context,
                                                                                "이미 추가된 운동입니다.",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                        } else {
                                                                            val NewjsonDataList =
                                                                                jsonDataList + data
                                                                            file.writeText(
                                                                                Gson().toJson(
                                                                                    NewjsonDataList
                                                                                )
                                                                            )
                                                                            Toast.makeText(
                                                                                context,
                                                                                "운동을 추가하였습니다.",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                        }
                                                                    } else {
                                                                        val data = todo(
                                                                            currentDate,
                                                                            textboxExercise,
                                                                            textboxNotes
                                                                        )
                                                                        val NewjsonDataList =
                                                                            listOf(data)
                                                                        file.writeText(
                                                                            Gson().toJson(
                                                                                NewjsonDataList
                                                                            )
                                                                        )
                                                                        Toast.makeText(
                                                                            context,
                                                                            "운동을 추가하였습니다.",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                                    Updater()
                                                                    onClick()
                                                                } else {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "선택된 운동이 없습니다.",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
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
                                        ShowExercisesElegantly(
                                            Equipment = textboxEquip,
                                            Category = textboxCategory,
                                            currentExercise = textboxExercise,
                                            state = (ShowExercises && state),
                                            onClick = ExercisesUpdate,
                                            modifier = Modifier
                                        )
                                    }
                                }
                                CategoryToolBar(
                                    currentCategory = textboxCategory,
                                    listOf(
                                        "Abs", "Back", "Biceps", "Cardio", "Chest", "Forearms",
                                        "Glutes", "Shoulders", "Triceps", "Upper Legs", "Lower Legs"
                                    ),
                                    state = (ShowCategories && state),
                                    onClick = CategoryUpdate,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        EquipToolBar(
                            currentEquip = textboxEquip,
                            listOf(
                                "Body Weight", "Bands", "Barbell", "Bench", "Dumbbell", "Exercise Ball",
                                "EZ Curl Bar", "Foam Roll", "Kettlebell", "Cardio Machine", "Strength Machine",
                                "Pullup Bar", "Weight Plate"
                            ),
                            state = (ShowEquips && state),
                            onClick = EquipsUpdate,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShowExercisesElegantly(Equipment: String, Category: String, currentExercise: String, state: Boolean, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    val context = LocalContext.current
    val jsonString =
        context.resources.openRawResource(R.raw.jefit_exercises_final).bufferedReader().use {
            it.readText()
        }

    val gson = Gson()
    val exercises = gson.fromJson(jsonString, Array<Exercise>::class.java).toList()

    var exercisesList by remember { mutableStateOf(exercises) }

    exercisesList = exercises.filter {
        if (Equipment != "") {
            it.equipment == Equipment
        } else true
    }.filter {
        if (Category != "") {
            it.body_part == Category
        } else true
    }.filter {
        if (it.name.length >= currentExercise.length) {
            it.name.slice(0..(currentExercise.length-1)).lowercase() == currentExercise.lowercase()
        }
        else {
            currentExercise.slice(0..(it.name.length-1)).lowercase() == it.name.lowercase()
        }
    }

    ExerciseToolBar(currentExercise, exercisesList, state, onClick = onClick)
}


@Composable
fun ExerciseToolBar(currentExercise: String, list: List<Exercise>, state: Boolean, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    val lazyListState = rememberLazyListState()

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
            LazyColumn (
                state = lazyListState,
                modifier = Modifier.height(200.dp)
            ){
                items(list) { item ->
                    Box(
                        modifier = Modifier
                            .shadow(3.dp, shape = RoundedCornerShape(8.dp))
                            .height(34.dp)
                            .width(347.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F8FF))
                            .clickable() {
                                onClick(item.name)
                            }
                    ) {
                        Text(
                            text = " ${item.name}",
                            fontSize = 20.sp,
                            fontWeight = if (currentExercise == item.name) FontWeight.Bold else FontWeight.Light,
                            textAlign = TextAlign.Left,
                        )
                    }
                }
            }
        }
    }
}