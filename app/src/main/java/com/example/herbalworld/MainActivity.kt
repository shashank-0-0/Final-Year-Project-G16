package com.example.herbalworld

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.herbalworld.Model.network.model.Herb
import com.example.herbalworld.UiLayer.HomeViewModel
import com.example.herbalworld.UiLayer.search.SearchScreen
import com.example.herbalworld.ui.theme.HerbalWorldTheme


class MainActivity : ComponentActivity() {

    private val homeViewModel by viewModels<HomeViewModel>()

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {

            HerbalWorldTheme {
                // A surface container using the 'background' color from the theme
                val state=homeViewModel.herbs.observeAsState()
                var isBottomSheetExpanded by remember { mutableStateOf(false) }
                val page=homeViewModel.page.value
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    var screenState by remember{
                        mutableStateOf(Tab.Home)
                    }
                    if(screenState==Tab.Home){
                        UI(
                            herbs = state.value ?: emptyList(),
                            onClick = {},
                            onLoadMore = {
                                homeViewModel.nextPage()
                            }, page = page
                        )
                    }else{
                        SearchScreen(
                            onCameraClick = {
                                Log.i("shetty","Hmmmmmmmmm")
                                isBottomSheetExpanded = true
                            },
                            onGalleryClick = {}
                        )
                    }
                    BottomBar(
                        selectedTab = screenState,
                        onHomeClick = {
                            screenState=Tab.Home
                        },
                        onFavoritesClick = {
                            screenState=Tab.Search
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun UI(
    herbs: List<Herb>,
    onClick: (Herb) -> Unit,
    onLoadMore:()->Unit,
    page:Int
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color.Black, Color.DarkGray)))
    ) {
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier
                .padding(start = 15.dp)
                .fillMaxWidth(),
            text = "Herbs",
            fontSize = 30.sp,
            color = Color.White
        )
        Spacer(Modifier.height(10.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2)){
            items(herbs.size){
                ImageCard(
                    imageUrl = herbs[it].image_url,
                    imageTitle = herbs[it].common_name,
                )
                Log.i("shetty","?? ${it} $page")

                if(it>=page*19){
                    onLoadMore().also {
                        Log.i("shetty","load")
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    if(herbs.isEmpty()){
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                color = Color.Green
            )

        }
    }

}

@Composable
fun ImageCard(
    imageUrl:String,
    imageTitle: String,
    modifier:Modifier=Modifier
){
    Card(
        modifier= modifier
            .padding(start=12.dp,end=12.dp,top=12.dp,bottom=1.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Box(
            modifier=Modifier
                .height(200.dp)
        ){
            AsyncImage(
                model = imageUrl,
                contentDescription = "An Image",
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = 300f
                        )
                    )
            )
            Box(
                modifier= Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomStart
            ){
                Text(
                    text = imageTitle,
                    style = TextStyle(
                        color=Color.White,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HerbalWorldTheme {

    }

}


enum class Tab {
    Home, Search
}

@Composable
fun BoxScope.BottomBar(
    selectedTab: Tab,
    modifier: Modifier = Modifier.align(Alignment.BottomCenter),
    onHomeClick: () -> Unit,
    onFavoritesClick: () -> Unit,
) {
    BottomBarRow(
        selectedTab = selectedTab,
        modifier=modifier,
        onHomeClick = onHomeClick,
        onAccountsClick = onFavoritesClick
    )

}

@Composable
private fun BottomBarRow(
    selectedTab: Tab,
    modifier: Modifier,
    onHomeClick: () -> Unit,
    onAccountsClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.DarkGray,
                shape = RoundedCornerShape(24.dp)
            )
            .border(1.dp, Color.Black, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Tab(
            text = "Home",
            selected = selectedTab == Tab.Home,
            icon = Icons.Outlined.Home,
            modifier = Modifier.weight(1f),
            onClick = onHomeClick
        )
        Spacer(Modifier.width(8.dp))
        Tab(
            text = "Search",
            selected = selectedTab == Tab.Search,
            icon = Icons.Rounded.Search,
            modifier = Modifier.weight(1f),
            onClick = onAccountsClick,
        )
    }
}

@Composable
private fun Tab(
    text: String,
    selected: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconRes(
            icon = icon,
            tint = if (selected) Color.Blue else Color.Red,
        )
        if (selected) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White
            )
        }
    }
}

@Composable
fun IconRes(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    contentDescription: String = "icon"
) {
    Icon(
        imageVector = icon,
        contentDescription = "Bottom Bar Image"
    )
}



