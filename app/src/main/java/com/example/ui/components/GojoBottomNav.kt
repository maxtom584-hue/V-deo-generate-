package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.GojoScreenTab

@Composable
fun GojoBottomNav(
    currentTab: GojoScreenTab,
    unclaimedTasksCount: Int,
    onTabSelected: (GojoScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("gojo_bottom_navigation"),
        containerColor = Color(0xFF0F121C),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == GojoScreenTab.FEED,
            onClick = { onTabSelected(GojoScreenTab.FEED) },
            icon = {
                Icon(
                    imageVector = if (currentTab == GojoScreenTab.FEED) Icons.Default.PlayCircleFilled else Icons.Outlined.PlayCircleOutline,
                    contentDescription = "Social Feed",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Feed",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == GojoScreenTab.FEED) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF00E5FF),
                selectedTextColor = Color(0xFF00E5FF),
                indicatorColor = Color(0xFF00E5FF).copy(alpha = 0.15f),
                unselectedIconColor = Color(0xFF8E95AB),
                unselectedTextColor = Color(0xFF8E95AB)
            ),
            modifier = Modifier.testTag("nav_feed_tab")
        )

        NavigationBarItem(
            selected = currentTab == GojoScreenTab.CREATE,
            onClick = { onTabSelected(GojoScreenTab.CREATE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == GojoScreenTab.CREATE) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome,
                    contentDescription = "Create Video",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Studio",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == GojoScreenTab.CREATE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF00E5FF),
                selectedTextColor = Color(0xFF00E5FF),
                indicatorColor = Color(0xFF00E5FF).copy(alpha = 0.15f),
                unselectedIconColor = Color(0xFF8E95AB),
                unselectedTextColor = Color(0xFF8E95AB)
            ),
            modifier = Modifier.testTag("nav_create_tab")
        )

        NavigationBarItem(
            selected = currentTab == GojoScreenTab.TASKS,
            onClick = { onTabSelected(GojoScreenTab.TASKS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unclaimedTasksCount > 0) {
                            Badge(
                                containerColor = Color(0xFFFFD700),
                                contentColor = Color.Black
                            ) {
                                Text(
                                    text = "$unclaimedTasksCount",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == GojoScreenTab.TASKS) Icons.Default.MonetizationOn else Icons.Outlined.MonetizationOn,
                        contentDescription = "Tasks & Coins",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    text = "Gojo Coins",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == GojoScreenTab.TASKS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFD700),
                selectedTextColor = Color(0xFFFFD700),
                indicatorColor = Color(0xFFFFD700).copy(alpha = 0.15f),
                unselectedIconColor = Color(0xFF8E95AB),
                unselectedTextColor = Color(0xFF8E95AB)
            ),
            modifier = Modifier.testTag("nav_tasks_tab")
        )

        NavigationBarItem(
            selected = currentTab == GojoScreenTab.PROFILE,
            onClick = { onTabSelected(GojoScreenTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == GojoScreenTab.PROFILE) Icons.Default.Person else Icons.Outlined.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Profile",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == GojoScreenTab.PROFILE) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF00E5FF),
                selectedTextColor = Color(0xFF00E5FF),
                indicatorColor = Color(0xFF00E5FF).copy(alpha = 0.15f),
                unselectedIconColor = Color(0xFF8E95AB),
                unselectedTextColor = Color(0xFF8E95AB)
            ),
            modifier = Modifier.testTag("nav_profile_tab")
        )
    }
}
