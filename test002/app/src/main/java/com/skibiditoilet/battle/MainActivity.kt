package com.skibiditoilet.battle

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skibiditoilet.battle.ui.theme.SkibidiToiletBattleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkibidiToiletBattleTheme {
                MainMenuScreen()
            }
        }
    }
}

@Composable
fun MainMenuScreen() {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Game Title
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            // Menu Buttons
            MenuButton(
                text = stringResource(R.string.single_player),
                onClick = {
                    val intent = Intent(context, GameActivity::class.java)
                    intent.putExtra("gameMode", "single")
                    context.startActivity(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MenuButton(
                text = stringResource(R.string.multiplayer),
                onClick = {
                    val intent = Intent(context, GameActivity::class.java)
                    intent.putExtra("gameMode", "multiplayer")
                    context.startActivity(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MenuButton(
                text = stringResource(R.string.character_select),
                onClick = {
                    // TODO: Navigate to character selection screen
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MenuButton(
                text = stringResource(R.string.settings),
                onClick = {
                    // TODO: Navigate to settings screen
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            MenuButton(
                text = stringResource(R.string.exit),
                onClick = {
                    (context as ComponentActivity).finish()
                },
                backgroundColor = Color(0xFF8B0000)
            )
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color(0xFF4A90E2)
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    SkibidiToiletBattleTheme {
        MainMenuScreen()
    }
}
