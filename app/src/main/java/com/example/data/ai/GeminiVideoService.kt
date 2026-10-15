package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiVideoService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    data class VideoScenePlan(
        val enhancedPrompt: String,
        val cameraMotion: String,
        val visualEffects: List<String>,
        val soundscape: String
    )

    suspend fun enhanceVideoPrompt(
        userPrompt: String,
        style: String,
        cameraMotion: String
    ): VideoScenePlan = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackPlan(userPrompt, style, cameraMotion)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            
            val systemInstruction = "You are Gojo Video's AI director. Expand the user's video prompt into a concise cinematic visual script for an AI video model. Return JSON with keys: enhancedPrompt (string), cameraMotion (string), visualEffects (array of 3 strings), soundscape (string)."

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "Style: $style\nCamera Motion: $cameraMotion\nUser Prompt: $userPrompt")
                            })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemInstruction)
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val respString = response.body?.string() ?: ""
                val root = JSONObject(respString)
                val text = root.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!text.isNullOrBlank()) {
                    val parsed = JSONObject(text)
                    val vfxList = mutableListOf<String>()
                    val vfxArray = parsed.optJSONArray("visualEffects")
                    if (vfxArray != null) {
                        for (i in 0 until vfxArray.length()) {
                            vfxList.add(vfxArray.getString(i))
                        }
                    } else {
                        vfxList.addAll(listOf("Particle aura", "Chromatic glow", "Volumetric lighting"))
                    }

                    return@withContext VideoScenePlan(
                        enhancedPrompt = parsed.optString("enhancedPrompt", userPrompt),
                        cameraMotion = parsed.optString("cameraMotion", cameraMotion),
                        visualEffects = vfxList,
                        soundscape = parsed.optString("soundscape", "Synth anime ambient swoosh")
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiVideoService", "Failed to call Gemini API", e)
        }

        return@withContext fallbackPlan(userPrompt, style, cameraMotion)
    }

    private fun fallbackPlan(userPrompt: String, style: String, cameraMotion: String): VideoScenePlan {
        val enhanced = when (style.lowercase()) {
            "anime" -> "Dynamic anime animation sequence: $userPrompt, vibrant hand-drawn keyframes, glowing energy lines, crisp cell shading, 60fps cinematic fluidity."
            "cyberpunk" -> "High-contrast cyberpunk scene: $userPrompt, rain-drenched neon reflections, volumetric haze, futuristic holographic HUD elements, anamorphic lens flare."
            "cinematic" -> "Panavision 35mm anamorphic footage: $userPrompt, shallow depth of field, dramatic rim lighting, natural motion blur, 4K film grain."
            "fantasy" -> "High fantasy realm rendering: $userPrompt, ethereal luminescence, magical particulate drifts, glowing runes, sweeping vista scale."
            else -> "High-fidelity generated video: $userPrompt, rendered with realistic fluid physics, dramatic lighting, and smooth $cameraMotion movement."
        }

        val vfx = when (style.lowercase()) {
            "anime" -> listOf("Blue Domain Sparkles", "Action Lines", "Speed Blur")
            "cyberpunk" -> listOf("Neon Puddle Ripples", "Steam Haze", "Glitch Flicker")
            "fantasy" -> listOf("Floating Mana Orbs", "Sunbeam Rays", "Shimmer Dust")
            else -> listOf("Cinematic Grain", "Depth of Field", "Dynamic Lighting")
        }

        return VideoScenePlan(
            enhancedPrompt = enhanced,
            cameraMotion = cameraMotion,
            visualEffects = vfx,
            soundscape = "Cinematic bass swell with subtle ambient atmospheric motion"
        )
    }
}
