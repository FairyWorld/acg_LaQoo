package com.laqoome.laqoo.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.laqoome.laqoo.presentation.screen.detail.laqooDetailScreen
import com.laqoome.laqoo.presentation.screen.download.DownloadScreen
import com.laqoome.laqoo.presentation.screen.downloaddetail.DownloadDetailScreen
import com.laqoome.laqoo.presentation.screen.history.HistoryScreen
import com.laqoome.laqoo.presentation.screen.main.MainScreen
import com.laqoome.laqoo.presentation.screen.search.SearchScreen
import com.laqoome.laqoo.presentation.screen.settings.AppearanceScreen
import com.laqoome.laqoo.presentation.screen.settings.DanmakuSettingsScreen
import com.laqoome.laqoo.presentation.screen.videoplayer.VideoPlayScreen
import com.laqoome.laqoo.util.SourceMode

@Composable
fun laqooNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onNavigateTolaqooDetail: (detailUrl: String, mode: SourceMode) -> Unit,
    onNavigateToVideoPlay: (parameters: String) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDownload: () -> Unit,
    onNavigateToDownloadDetail: (detailUrl: String, title: String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToDanmakuSettings: () -> Unit,
    onBackClick: () -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Main,
    ) {
        composable<Screen.Main> {
            MainScreen(
                onNavigateTolaqooDetail = onNavigateTolaqooDetail,
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToDownload = onNavigateToDownload,
                onNavigateToAppearance = onNavigateToAppearance,
                onNavigateToDanmakuSettings = onNavigateToDanmakuSettings,
            )
        }
        composable<Screen.laqooDetail> {
            laqooDetailScreen(
                onRelatedlaqooClick = onNavigateTolaqooDetail,
                onNavigateToVideoPlay = onNavigateToVideoPlay,
                onBackClick = onBackClick
            )
        }
        composable<Screen.VideoPlayer>(
            enterTransition = { fadeIn() },
            popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 35)) }
        ) {
            VideoPlayScreen(onBackClick = onBackClick)
        }
        composable<Screen.Search> {
            SearchScreen(
                onNavigateTolaqooDetail = onNavigateTolaqooDetail,
                onBackClick = onBackClick
            )
        }
        composable<Screen.HistoryScreen> {
            HistoryScreen(
                onBackClick = onBackClick,
                onNavigateTolaqooDetail = onNavigateTolaqooDetail,
            )
        }
        composable<Screen.Download> {
            DownloadScreen(
                onBackClick = onBackClick,
                onNavigateToDownloadDetail = onNavigateToDownloadDetail,
                onNavigateTolaqooDetail = onNavigateTolaqooDetail
            )
        }
        composable<Screen.DownloadDetail> {
            DownloadDetailScreen(
                onBackClick = onBackClick,
                onNavigateToVideoPlay = onNavigateToVideoPlay
            )
        }
        composable<Screen.Appearance> {
            AppearanceScreen(
                onBackClick = onBackClick
            )
        }
        composable<Screen.DanmakuSettings> {
            DanmakuSettingsScreen(
                onBackClick = onBackClick
            )
        }
    }
}