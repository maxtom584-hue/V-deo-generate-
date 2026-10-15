package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.VideoEntity
import com.example.ui.components.VideoPlayerCard

data class ImagePreset(
    val id: String,
    val title: String,
    val resName: String,
    val suggestedPrompt: String,
    val style: String
)

@Composable
fun CreateStudioScreen(
    promptInput: String,
    titleInput: String,
    selectedStyle: String,
    selectedCameraMotion: String,
    selectedAspectRatio: String,
    selectedDuration: Int,
    selectedPresetRes: String,
    coinsBalance: Int,
    isGenerating: Boolean,
    generationStep: String,
    showPreview: Boolean,
    lastGeneratedVideo: VideoEntity?,
    onPromptChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onStyleChange: (String) -> Unit,
    onMotionChange: (String) -> Unit,
    onAspectRatioChange: (String) -> Unit,
    onDurationChange: (Int) -> Unit,
    onPresetSelect: (ImagePreset) -> Unit,
    onGenerateClick: () -> Unit,
    onPostToFeed: () -> Unit,
    onDismissPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val presets = listOf(
        ImagePreset(
            id = "preset_infinity",
            title = "Domain Void",
            resName = "video_thumb_infinity_1788966908207",
            suggestedPrompt = "Gojo unleashing Infinite Void, starry cosmos unfolding, glowing cyan eyes, floating in boundless space with azure particle auras",
            style = "Anime"
        ),
        ImagePreset(
            id = "preset_cyber",
            title = "Neon Tokyo",
            resName = "video_thumb_cyber_1788966923703",
            suggestedPrompt = "Cyberpunk anime protagonist speeding on futuristic hover-bike through rain-slicked neon street, holographic ads and reflection puddles",
            style = "Cyberpunk"
        ),
        ImagePreset(
            id = "preset_coin",
            title = "Gojo Token",
            resName = "gojo_coin_icon_1788966884004",
            suggestedPrompt = "Glowing 3D Gojo Coin spinning in zero gravity with neon cyan energy lightning arcs, cinematic 60fps render",
            style = "Cinematic"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Studio Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI VIDEO GENERATOR",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "Transform images & prompts into cinematic motion",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF00E5FF)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF161B2E),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Cost: 10 Coins",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        // Section 1: Source Image Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1. Select Base Visual / Scene",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Pick a starter scene",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8E95AB)
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presets, key = { it.id }) { preset ->
                    val isSelected = selectedPresetRes == preset.resName
                    val resId = remember(preset.resName) {
                        context.resources.getIdentifier(preset.resName, "drawable", context.packageName)
                    }

                    Card(
                        modifier = Modifier
                            .width(110.dp)
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onPresetSelect(preset) }
                            .testTag("preset_${preset.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B2E))
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (resId != 0) {
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = preset.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            // Bottom Label
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                            ) {
                                Text(
                                    text = preset.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                                    maxLines = 1
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E5FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Prompt Input
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "2. Prompt & Video Title",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            OutlinedTextField(
                value = titleInput,
                onValueChange = onTitleChange,
                label = { Text("Video Title (Optional)") },
                placeholder = { Text("e.g. Limitless Expansion") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("video_title_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00E5FF),
                    unfocusedBorderColor = Color(0xFF2B324B),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = promptInput,
                onValueChange = onPromptChange,
                label = { Text("Motion & Scene Prompt") },
                placeholder = { Text("Describe the action, movements, camera angles, and energy effects...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prompt_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00E5FF),
                    unfocusedBorderColor = Color(0xFF2B324B),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Quick Prompt Ideas
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val suggestions = listOf(
                    "Gojo Hollow Purple burst",
                    "Cyberpunk lightcycle rain skid",
                    "Samurai katana slice with sakura leaves",
                    "Cosmic meteor shower orbit"
                )
                items(suggestions) { sug ->
                    SuggestionChip(
                        onClick = { onPromptChange(sug) },
                        label = { Text(sug, fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color(0xFF171D30),
                            labelColor = Color(0xFF00E5FF)
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = Color(0xFF00E5FF).copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }

        // Section 3: Visual Style & Camera Motion
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "3. Cinematic Settings",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            // Style Selector
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Art Style",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8E95AB)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Anime", "Cyberpunk", "Cinematic", "Fantasy").forEach { style ->
                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = { onStyleChange(style) },
                            label = { Text(style) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                selectedLabelColor = Color(0xFF00E5FF)
                            ),
                            modifier = Modifier.testTag("style_chip_$style")
                        )
                    }
                }
            }

            // Camera Motion Selector
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Camera Motion",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8E95AB)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Dynamic Zoom", "Pan Right", "Orbit", "Slow Motion").forEach { motion ->
                        FilterChip(
                            selected = selectedCameraMotion == motion,
                            onClick = { onMotionChange(motion) },
                            label = { Text(motion) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFB388FF).copy(alpha = 0.2f),
                                selectedLabelColor = Color(0xFFB388FF)
                            ),
                            modifier = Modifier.testTag("motion_chip_${motion.replace(" ", "_")}")
                        )
                    }
                }
            }

            // Aspect Ratio & Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Aspect Ratio
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Aspect Ratio",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8E95AB)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("9:16", "16:9", "1:1").forEach { ratio ->
                            FilterChip(
                                selected = selectedAspectRatio == ratio,
                                onClick = { onAspectRatioChange(ratio) },
                                label = { Text(ratio) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFF00E5FF)
                                )
                            )
                        }
                    }
                }

                // Duration
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Duration",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8E95AB)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(3, 5, 8).forEach { sec ->
                            FilterChip(
                                selected = selectedDuration == sec,
                                onClick = { onDurationChange(sec) },
                                label = { Text("${sec}s") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFFD700).copy(alpha = 0.2f),
                                    selectedLabelColor = Color(0xFFFFD700)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Generate Action Button
        val hasEnoughCoins = coinsBalance >= 10
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(
                onClick = onGenerateClick,
                enabled = !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_video_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF00363D),
                    disabledContainerColor = Color(0xFF1D2636)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "GENERATE VIDEO (10 COINS)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
                    )
                }
            }

            if (!hasEnoughCoins) {
                Text(
                    text = "Insufficient Gojo Coins ($coinsBalance/10). Complete tasks or ask Admin to unlock more!",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Modal: Live Generation Progress Overlay
    if (isGenerating) {
        Dialog(
            onDismissRequest = { /* Non-dismissible while generating */ },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
                    .testTag("generation_progress_dialog"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121626)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF00E5FF),
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = "Synthesizing AI Video",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Text(
                        text = generationStep,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF00E5FF),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF00E5FF),
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }

    // Modal: Generated Video Preview Dialog
    if (showPreview && lastGeneratedVideo != null) {
        Dialog(
            onDismissRequest = onDismissPreview,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight(0.92f)
                    .padding(vertical = 12.dp)
                    .testTag("generated_video_preview_dialog"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101422))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Generation Complete!",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        IconButton(onClick = onDismissPreview) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }

                    // Video Card Viewport
                    Box(modifier = Modifier.weight(1f)) {
                        VideoPlayerCard(
                            video = lastGeneratedVideo,
                            onLikeClick = { /* preview mode */ },
                            onCommentClick = { /* preview mode */ },
                            onRemixClick = { /* preview mode */ }
                        )
                    }

                    // Post to Social Network Feed CTA
                    Button(
                        onClick = onPostToFeed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("post_to_feed_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color.Black
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Post to Community Feed (+20 Gojo Coins!)",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onDismissPreview,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Keep in Drafts")
                    }
                }
            }
        }
    }
}
