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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                bottom = 105.dp
            ),
    ) {
        GalleryContentList(
            affirmationList = Datasource().loadAffirmations(),
            onClick = { showSingleImageBox = !showSingleImageBox }
        )
        AnimatedVisibility(
            visible = showSingleImageBox,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }
            ),
            exit = slideOutVertically (
                targetOffsetY  = { fullHeight -> fullHeight }
            )
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.Red)
                    .clickable { showSingleImageBox = !showSingleImageBox },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF9D9D9D))
                    )
                    Box() {
                        Text("메인 사진 프레임")
                    }
                    Box() {
                        Text("운동 기록 리스트")
                    }
                    Row() {
                        Button(
                            onClick = { showSingleImageBox = !showSingleImageBox },
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text("확인")
                        }
                        Button(
                            {},
                            modifier = Modifier.padding(10.dp)
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
                modifier = Modifier.aspectRatio(109f/95f),
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