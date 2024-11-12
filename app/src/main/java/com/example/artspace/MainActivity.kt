package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.artspace.data.DataSource
import com.example.artspace.ui.theme.ArtSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ArtSpaceTheme {

                NavHost(navController = navController, startDestination = Screen.Home.route + "/{id}"){

                    //this is the home page composable
                    composable(
                        Screen.Home.route + "/{id}", arguments = listOf(navArgument("id"){
                            type = NavType.IntType
                            defaultValue = 0
                        })
                    ){
                            HomePage( navController = navController)
                        }

                        //this is the artist page composable
                    composable(
                        Screen.Artist.route + "/{id}",
                        arguments = listOf(navArgument("id"){type = NavType.IntType })
                    ){
                        ArtistPage( navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun ArtistPage(navController: NavController){

    val id = navController.currentBackStackEntry?.arguments?.getInt("id") ?: 0
    val art = DataSource.arts[id]

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(top = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = art.artistImageId),
                contentDescription = stringResource(id = art.artistId),
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp)
                    .border(3.dp, Color.Black, CircleShape)
                    .padding(5.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(top = 30.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = art.artistId),
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "(" + stringResource(id = art.artistInfoId) + ")",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

            }
        }


        Text(
            text = stringResource(id = art.artistBioId),
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .padding(start = 8.dp),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.small)))

        Button(onClick = { navController.navigate(Screen.Home.route + "/$id") }, modifier = Modifier.padding(start = 8.dp)) {
            Text(text = stringResource(id = R.string.back))
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage (navController: NavController){
    var current by remember{
        mutableIntStateOf(
            navController.currentBackStackEntry?.arguments?.getInt("id") ?: 0
        )
    }
    val art = DataSource.arts[current]

    Scaffold( topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

        }
    ) { innerPadding ->

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ){

            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacing_extra_large)))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                ArtWall(artistId = current, artImageId = art.artworkImageId, artDescriptionId = art.descriptionId, navController = navController)
            }
        }
        ArtDescriptor(artTitleId = art.titleId, artistId = art.artistId, artYearId = art.yearId)
        DisplayController(current = current){
            current = if (it !in 0..<DataSource.arts.size) 0 else it
        }
    }
    }
}

@Composable
fun ArtWall(artistId: Int, artImageId: Int, artDescriptionId: Int, navController: NavController){

        Box(
            modifier = Modifier
                .size(width = 300.dp, height = 300.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .clickable { navController.navigate(Screen.Artist.route + "/$artistId") }
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = painterResource(id = artImageId),
                contentDescription = stringResource(id = artDescriptionId),
                contentScale = ContentScale.FillBounds
            )
        }

}

@Composable
fun ArtDescriptor(artTitleId: Int, artistId: Int, artYearId: Int){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 75.dp , vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ){
        Text(
            modifier = Modifier
                .padding(bottom = 4.dp),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            text = stringResource(id = artTitleId),
        )
        Text(
            modifier = Modifier
                .padding(bottom = 4.dp),
            fontSize = 15.sp,
            text = stringResource(id = artistId) + " (" + stringResource(id = artYearId) + ")"
        )
    }
}

@Composable
fun DisplayController(current: Int, updateCurrent: (Int) -> Unit){

    Row(
        modifier = Modifier
            .padding(horizontal = 50.dp)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Button(
            onClick = {updateCurrent(current - 1)},
            modifier = Modifier
                .padding(16.dp)
                .size(width = 120.dp, height = 50.dp),
            enabled = current != 0
        ) {
            Text(text = "Previous")
        }
        Button(
            onClick = {updateCurrent(current + 1)},
            modifier = Modifier
                .padding(16.dp)
                .size(width = 200.dp, height = 50.dp),
            enabled = current != DataSource.arts.size-1
        ) {
            Text(text = "Next")
        }

    }
}


