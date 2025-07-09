package com.example.realhealth.ui.Gallery

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import coil.compose.rememberAsyncImagePainter
import com.example.realhealth.R
import com.example.realhealth.databinding.FragmentGalleryBinding
import com.example.realhealth.model.todo
import com.example.realhealth.ui.ShowCalender.CalenderMain
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import kotlin.math.roundToInt

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.composeViewGallery.setContent {
            MainApp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun MainApp() {
    val layoutDirection = LocalLayoutDirection.current
    var showSingleImageBox by remember { mutableStateOf(false) }
    var showAddingImageBox by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val directory = context.filesDir
    var file_list by remember { mutableStateOf<List<File>>(emptyList()) }

    var SingleImage by remember { mutableStateOf<File?>(null) }

    val update_file_list = {
        file_list = directory.listFiles()?.toList() ?: emptyList()
        if (file_list.size > 0) {
            file_list = file_list.filter {
                it.isFile && it.name.startsWith("MYIMG_") && it.name.endsWith(".jpg") && (it.name.length > 17)}
            file_list = file_list.sortedByDescending { it.name.slice(6..14).toInt() }
        }
        println(file_list)
    }

    val Select_SingleImage = { input: String ->
        SingleImage = File(context.filesDir, input)
    }

    val onSearch = { input: String ->
        val RealString = input.trimStart().trimEnd()
        val FormatDate = Regex("^\\d{4}\\.(0[1-9]|1[0-2])\\.(0[1-9]|[12][0-9]|3[01])$")
        if (RealString.matches(FormatDate)) {
            val search_list = file_list.filter {
                it.name.slice(6..13) == RealString.slice(0..3) + RealString.slice(5..6) + RealString.slice(8..9)
            }
            if (search_list.size == 0) {
                Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Select_SingleImage("MYIMG_" + RealString.slice(0..3) + RealString.slice(5..6) + RealString.slice(8..9) + ".jpg")
                showSingleImageBox = true
            }
        } else {
            Toast.makeText(context, "YYYY.MM.DD 형식으로 적어주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        update_file_list()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
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
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
                .background(color = Color.White)
        ) {
            Box(modifier = Modifier.height(10.dp))
            Row() {
                Box(modifier = Modifier.width(10.dp))
                SearchGalleryImageByCurrentDate(onSearch = onSearch, modifier = Modifier.weight(1f))
                Box(modifier = Modifier.width(10.dp))
            }
            Box(modifier = Modifier.height(5.dp))
            GalleryContentList(
                onClick = { if (!showAddingImageBox) showSingleImageBox = !showSingleImageBox },
                file_list,
                Select_SingleImage
            )
            if (file_list.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize().background(color = Color.White)
                ) {
                    Text(
                        text = "사진이 존재하지 않습니다.",
                        fontSize = 18.sp
                    )
                    Box(modifier = Modifier.height(3.dp))
                    Text(
                        text = "업로드한 사진을 여기에서 한 번에 볼 수 있습니다.",
                        fontSize = 10.sp
                    )
                }
            }
        }
        AddImageButton(
            state = showAddingImageBox or showSingleImageBox
        ) { if (!showSingleImageBox) showAddingImageBox = !showAddingImageBox }
        MainSingleImage(
            state = showSingleImageBox,
            file = SingleImage,
            Updater = update_file_list,
            onClick = { showSingleImageBox = !showSingleImageBox }
        )
        MainAddingImage(
            state = showAddingImageBox,
            Updater = update_file_list,
            onClick = { showAddingImageBox = !showAddingImageBox }
        )
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
        if (showSingleImageBox) showSingleImageBox = false
        else if (showAddingImageBox) showAddingImageBox = false
        else navController.navigate(R.id.navigation_home)
    }
}

@Composable
fun SearchGalleryImageByCurrentDate(modifier: Modifier = Modifier, onSearch: (String) -> Unit) {
    var textState by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier.height(43.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(color = Color(0xFFD9D9D9))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(R.drawable.dotbogi),
                modifier = Modifier.size(23.dp),
                contentDescription = null
            )
            Box(modifier = Modifier.width(5.dp))
            BasicTextField(
                value = textState,
                onValueChange = { textState = it },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    textAlign = TextAlign.Left,
                    color = Color(0xFFFFFFFF),
                ),
                decorationBox = { innerTextField ->
                    Box() {
                        if (textState.isEmpty()) {
                            Text(
                                text = "Search (YYYY.MM.DD)",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Left,
                                color = Color(0xFFFFFFFF),
                            )
                        }
                        innerTextField()
                    }
                },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onSearch(textState)
                    }
                ),
                modifier = Modifier
                    .weight(1f)
            )
            Box(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
fun AddImageButton(state: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
fun MainAddingImage(state: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit, Updater: () -> Unit) {
    val calender = java.util.Calendar.getInstance()
    var currentDate by remember { mutableStateOf(String.format("%04d", calender.get(Calendar.YEAR)) + "." + String.format("%02d", calender.get(Calendar.MONTH) + 1) + "." + String.format("%02d", calender.get(Calendar.DAY_OF_MONTH))) }

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

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.size > 5) {
            android.widget.Toast.makeText(context, "사진은 최대 5장까지만 선택됩니다.", Toast.LENGTH_LONG).show()
            selectedImageUris = uris.take(5)
        } else {
            selectedImageUris = uris
        }
    }

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
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(17.dp))
                            .onSizeChanged { size: IntSize ->
                                weightedCalenderHeightdp = (size.height / density).toInt()
                                println("$weightedCalenderWidthdp, $weightedCalenderHeightdp")
                            }
                    ) {
                        CalenderMain(currentDate = currentDate, currentDateUpdate = currentDateUpdate, 349, weightedCalenderHeightdp, Color(0xFF1565C0))
                    }
                    Box(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier.height(212.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyVerticalGrid(
                            modifier = Modifier.padding(top = 0.dp, bottom = 0.dp, start = 10.dp, end = 10.dp),
                            columns = GridCells.Fixed(3),
                            userScrollEnabled = false,
                        ) {
                            items(listOf(0, 1, 2, 3, 4, 5)) { item ->
                                Image(
                                    painter = when (item) {
                                        0 -> painterResource(R.drawable.image_adder)
                                        else -> if (item-1 < selectedImageUris.size) rememberAsyncImagePainter(model = selectedImageUris[item-1])
                                                else painterResource(R.drawable.image_notyet)
                                    },
                                    contentDescription = item.toString(),
                                    modifier = Modifier
                                        .aspectRatio(109f / 95f)
                                        .width(95.dp)
                                        .height(109.dp)
                                        .padding(2.dp)
                                        .clickable(
                                            enabled = if (item == 0) buttonEnable else false,
                                            onClick = {
                                                if (item == 0) {
                                                    galleryLauncher.launch("image/*")
                                                }
                                            }),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier.height(5.dp))
                    Row() {
                        Button(
                            onClick = {
                                if (selectedImageUris.size > 0) {
                                    val filename = "MYIMG_${currentDate.slice(0..3)}${currentDate.slice(5..6)}${currentDate.slice(8..9)}"

                                    for (i in 0..selectedImageUris.size-1) {
                                        val file = File(context.filesDir, filename + i.toString() + ".jpg")
                                        try {
                                            val outputStream = FileOutputStream(file)
                                            val inputStream = context.contentResolver.openInputStream(selectedImageUris[i])
                                            inputStream.use { input ->
                                                outputStream.use { output ->
                                                    input?.copyTo(output)
                                                }
                                            }
                                            Toast.makeText(context, "${selectedImageUris.size}개의 파일을 저장하였습니다.", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "파일을 저장하지 못했습니다.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    selectedImageUris = emptyList()
                                    Updater()
                                    onClick()
                                } else {
                                    Toast.makeText(context, "선택된 파일이 없습니다.", Toast.LENGTH_SHORT).show()
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
                            Text("업로드")
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
    }
}

@Composable
fun MainSingleImage(state: Boolean, file: File?, modifier: Modifier = Modifier, onClick: () -> Unit, Updater: () -> Unit) {
    var offsetY by remember { mutableStateOf(0f) }

    val density = LocalDensity.current.density
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var FileCount = 0
    var FileIndex by remember { mutableStateOf(0) }

    var offsetX by remember { mutableStateOf(0f) }

    val name = file?.name?.slice(0..13) ?: ""
    val file1 = File(context.filesDir, name + "0.jpg")
    val file2 = File(context.filesDir, name + "1.jpg")
    val file3 = File(context.filesDir, name + "2.jpg")
    val file4 = File(context.filesDir, name + "3.jpg")
    val file5 = File(context.filesDir, name + "4.jpg")

    if (file5.exists()) FileCount = 5
    else if (file4.exists()) FileCount = 4
    else if (file3.exists()) FileCount = 3
    else if (file2.exists()) FileCount = 2
    else if (file1.exists()) FileCount = 1
    else FileCount = 0

    var textboxTag by remember { mutableStateOf("오운완") }

    val filename = "TodoData.json"
    val file = File(context.filesDir, filename)

    var jsonDataList: List<todo> by remember { mutableStateOf(emptyList()) }

    if (file.exists()) {
        val jsonStringDataList = file.readText()
        jsonDataList = Gson().fromJson(jsonStringDataList, Array<todo>::class.java).toList()
    } else {
        jsonDataList = emptyList()
    }

    val currentDate = if (name.length > 13) "${name.slice(6..9)}.${name.slice(10..11)}.${name.slice(12..13)}" else ""

    LaunchedEffect(state) {
        if (state) offsetY = 0f
        if (state) FileIndex = 0

        val file = File(context.filesDir, name + ".txt")
        if (file.exists()) {
            val text = file.readText()
            textboxTag = text
        } else {
            textboxTag = "오운완"
        }
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
                    onDragStopped = { velocity ->
                        println(velocity)
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
                                onDragStopped = { velocity ->
                                    println(velocity)
                                    if ((velocity > 8000f) or (offsetY / density > 270.dp.value)) {
                                        onClick()
                                    } else {
                                        offsetY = 0f
                                    }
                                }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier
                            .height(5.dp)
                            .width(370.dp))
                        Box(contentAlignment = Alignment.Center) {
                            Box(modifier = Modifier
                                .width(70.dp)
                                .height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF9D9D9D))
                                    .padding(100.dp)
                            )
                        }
                        Box(modifier = Modifier
                            .height(9.dp)
                            .width(370.dp))
                    }
                    Box() {
                        // 메인 그림
                        Box(
                            modifier = Modifier
                                .width(349.dp)
                                .height(304.dp)
                                .clip(RoundedCornerShape(17.dp))
                                .draggable(
                                    orientation = Orientation.Horizontal,
                                    state = rememberDraggableState { delta ->
                                        val max = if (FileIndex == 0) 0 else 349
                                        val min = if (FileIndex == FileCount - 1) 0 else -349
                                        if ((offsetX / density >= min) and (offsetX / density <= max)) {
                                            offsetX += delta
                                        }
                                        if (offsetX / density <= min) {
                                            offsetX = min * density
                                        }
                                        if (offsetX / density >= max) {
                                            offsetX = max * density
                                        }
                                    },
                                    onDragStopped = { velocity ->
                                        if ((velocity < -8000f) or (offsetX / density < -180.dp.value)) {
                                            offsetX = 0f
                                            if (FileIndex < FileCount-1) FileIndex += 1
                                        } else if ((velocity > 8000f) or (offsetX / density > 180.dp.value)) {
                                            offsetX = 0f
                                            if (FileIndex > 0) FileIndex -= 1
                                        }
                                        offsetX = 0f
                                    }
                                )
                        ) {
                            Image(
                                painter = if (file1.exists()) rememberAsyncImagePainter(model = file1) else painterResource(R.drawable.dataimage1),
                                contentDescription = "main image1",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(349.dp)
                                    .height(304.dp)
                                    .absoluteOffset(x = (0 - 349 * FileIndex).dp, y = 0.dp)
                                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                            )
                            Image(
                                painter = if (file2.exists()) rememberAsyncImagePainter(model = file2) else painterResource(R.drawable.dataimage1),
                                contentDescription = "main image2",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(349.dp)
                                    .height(304.dp)
                                    .absoluteOffset(x = (349 - 349 * FileIndex).dp, y = 0.dp)
                                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                            )
                            Image(
                                painter = if (file3.exists()) rememberAsyncImagePainter(model = file3) else painterResource(R.drawable.dataimage1),
                                contentDescription = "main image3",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(349.dp)
                                    .height(304.dp)
                                    .absoluteOffset(x = (349 * 2 - 349 * FileIndex).dp, y = 0.dp)
                                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                            )
                            Image(
                                painter = if (file4.exists()) rememberAsyncImagePainter(model = file4) else painterResource(R.drawable.dataimage1),
                                contentDescription = "main image4",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(349.dp)
                                    .height(304.dp)
                                    .absoluteOffset(x = (349 * 3 - 349 * FileIndex).dp, y = 0.dp)
                                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                            )
                            Image(
                                painter = if (file5.exists()) rememberAsyncImagePainter(model = file5) else painterResource(R.drawable.dataimage1),
                                contentDescription = "main image5",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(349.dp)
                                    .height(304.dp)
                                    .absoluteOffset(x = (349 * 4 - 349 * FileIndex).dp, y = 0.dp)
                                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.width(349.dp)
                        ) {
                            Box(modifier = Modifier.height(284.dp))
                            LazyRow(
                                modifier = Modifier.height(8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                items((0..FileCount-1).toList()) { item ->
                                    if (item != 0) Box(modifier = Modifier.size(5.dp))
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape)
                                        .background(color = if (FileIndex == item) Color(0xFF5E5757) else Color(0xFFECEFF1
                                        ))
                                        .border(width = 0.3.dp, color = Color.Black, shape = CircleShape))
                                }
                            }
                            Box(modifier = Modifier.height(12.dp))
                        }
                    }
                    Box(modifier = Modifier.height(9.dp))
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
                                text = "#",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Left
                            )
                            BasicTextField(
                                value = textboxTag,
                                onValueChange = { textboxTag = it },
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
                                modifier = Modifier
                                    .width(347.dp)
                            )
                        }
                    }
                    Box(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn {
                            items(jsonDataList) { data ->
                                if (data.date == currentDate) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .height(34.dp)
                                            .width(347.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFAEBEC4))
                                    ) {
                                        Text(
                                            text = data.name,
                                            color = Color.Black,
                                            fontSize = 20.sp,
                                            textAlign = TextAlign.Left,
                                        )
                                    }
                                    Box(modifier = Modifier.height(7.dp))
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.height(10.dp))
                    Row() {
                        Button(
                            onClick = {
                                onClick()
                                try {
                                    File(context.filesDir, name + ".txt").writeText(textboxTag)
                                } catch (e: Exception) {
                                    e.printStackTrace()
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
                            Text("확인")
                        }
                        Box(modifier = Modifier.width(25.dp))
                        Button(
                            onClick = {
                                if (file != null) {
                                    onClick()
                                    if (file1.exists()) {
                                        file1.delete()
                                        if (file2.exists()) {
                                            file2.delete()
                                            if (file3.exists()) {
                                                file3.delete()
                                                if (file4.exists()) {
                                                    file4.delete()
                                                    if (file5.exists()) {
                                                        file5.delete()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (File(context.filesDir, name + ".txt").exists()) {
                                        File(context.filesDir, name + ".txt").delete()
                                    }
                                    Toast.makeText(context, "파일을 삭제하였습니다.", Toast.LENGTH_SHORT).show()
                                    Updater()
                                }
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
                            Text("삭제")
                        }
                    }
                    Box(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}


@Composable
fun GalleryContentList(onClick: () -> Unit, file_list: List<File>, Select_SingleImage: (String) -> Unit, modifier: Modifier = Modifier) {
    val directory = LocalContext.current.filesDir

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.background(color = Color(0xFFFFFFFF))
    ) {
        items(file_list.filter {
            it.name[14].toString() == "0"
        }) { image ->
            GalleryImage(
                onClick = {
                    Select_SingleImage(image.name)
                    onClick()
                },
                file = image,
                modifier = Modifier.padding(3.dp)
            )
            println(image)
        }
    }
}

@Composable
fun GalleryImage(onClick: () -> Unit, file: File, modifier: Modifier = Modifier) {
    val imageSize = 194

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier.clickable {
            onClick()
        }
    ) {
        Box() {
            Image(
                painter = rememberAsyncImagePainter(model = file),
                contentDescription = file.name,
                modifier = Modifier.aspectRatio(1f/1f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = file.name.slice(6..9) + "." + file.name.slice(10..11) + "." + file.name.slice(12..13),
                    color = Color.White,
                    fontSize = (imageSize / 17).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color(0xBf000000))
                        .padding(start = 1.dp, end = 1.dp)
                )
            }
        }
    }
}