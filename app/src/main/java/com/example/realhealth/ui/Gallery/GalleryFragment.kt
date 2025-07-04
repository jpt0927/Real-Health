package com.example.realhealth.ui.Gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.realhealth.R
import com.example.realhealth.data.Datasource
import com.example.realhealth.databinding.FragmentGalleryBinding
import com.example.realhealth.model.Affirmation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
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

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
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

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxSize()
            .statusBarsPadding()
            .padding(
                start = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateStartPadding(layoutDirection) + 8.dp,
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(layoutDirection) + 8.dp,
                bottom = 104.dp
            ),
    ) {
        GalleryContentList(
            affirmationList = Datasource().loadAffirmations(),
            onClick = { showSingleImageBox = !showSingleImageBox }
        )
        MainSingleImage(
            state = showSingleImageBox
        ) { showSingleImageBox = !showSingleImageBox }
    }
}

@Composable
fun MainSingleImage(state: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    var offsetY by remember { mutableStateOf(0f) }
    val density = LocalDensity.current.density

    LaunchedEffect(state) {
        if (state) offsetY = 0f
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
                .offset { IntOffset(0, offsetY.roundToInt()) },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Box(modifier = Modifier.height(17.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
                    .width(370.dp)
                    .height(705.dp)
                    .background(Color(0xFFD9D9D9))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier
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
                            .width(70.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF9D9D9D))
                            .padding(100.dp)
                    )
                    Box(modifier = Modifier.height(9.dp))
                    Box() {
                        Image(
                            painter = painterResource(R.drawable.dataimage1),
                            contentDescription = "main image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.width(349.dp).height(304.dp)
                                .clip(RoundedCornerShape(17.dp))
                        )
                        Text("메인 사진 프레임")
                    }
                    Box(modifier = Modifier.height(9.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.height(34.dp)
                            .width(347.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFB0D2E0))
                    ) {
                        Text(
                            text = "운동 기록",
                            color = Color.Black,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Box(modifier = Modifier.height(13.dp))
                    Box(modifier = Modifier.height(160.dp)) {
                        LazyColumn {
                            items(listOf(1, 2, 3, 4, 5)) { exercise ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.height(34.dp)
                                        .width(347.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFAEBEC4))
                                ) {
                                    Text(
                                        text = "운동 기록: $exercise",
                                        color = Color.Black,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Left,
                                    )
                                }
                                Box(modifier = Modifier.height(7.dp))
                            }
                        }
                    }
                    Box(modifier = Modifier.height(13.dp))
                    Row() {
                        Button(
                            onClick = onClick,
                            modifier = Modifier.width(161.dp).height(34.44.dp),
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
                            {},
                            modifier = Modifier.width(161.dp).height(34.44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE0B0B1),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("삭제")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun GalleryContentList(onClick: () -> Unit, affirmationList: List<Affirmation>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(affirmationList) { image ->
            GalleryImage(
                onClick = onClick,
                affirmation = image,
                modifier = Modifier.padding(3.dp)
            )
        }
    }
}

@Composable
fun GalleryImage(onClick: () -> Unit, affirmation: Affirmation, modifier: Modifier = Modifier) {
    val imageSize = 194

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier.clickable {
            println("CLick ${affirmation.DataNum}!")
            onClick()
        }
    ) {
        Box() {
            Image(
                painter = painterResource(affirmation.imageResourceId),
                contentDescription = stringResource(affirmation.stringResourceId),
                modifier = Modifier.aspectRatio(1f/1f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = LocalContext.current.getString(affirmation.stringResourceId),
                    color = Color.Black,
                    fontSize = (imageSize / 25).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color(0xBfD9D9D9))
                        .size(width = 45.dp, height = (imageSize / 25 + 2).dp)
                )
            }
        }
    }
}