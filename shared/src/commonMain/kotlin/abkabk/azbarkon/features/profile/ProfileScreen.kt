package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.LocalAzbarkonAppState
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.core.uidata.asString
import abkabk.azbarkon.core.notifications.NotificationPermissionSheet
import abkabk.azbarkon.core.notifications.openAppNotificationSettings
import abkabk.azbarkon.core.notifications.rememberNotificationPermissionRequester
import abkabk.azbarkon.features.profile.util.rememberBackupImportLauncher
import abkabk.azbarkon.features.profile.util.showToast
import abkabk.azbarkon.ui.components.AzbarkonAlertDialog
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.clear_cancel
import azbarkoncmp.shared.generated.resources.clear_confirm
import azbarkoncmp.shared.generated.resources.profile_import_confirm_body
import azbarkoncmp.shared.generated.resources.profile_import_confirm_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoot(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<abkabk.azbarkon.core.uidata.UiText?>(null) }
    var showRemotePermissionSheet by remember { mutableStateOf(false) }
    val requestNotificationPermission =
        rememberNotificationPermissionRequester { granted ->
            viewModel.onAction(
                ProfileAction.OnNotificationPermissionResult(
                    granted,
                    NotificationPermissionTarget.DailyBeyt,
                ),
            )
        }
    val openImportPicker =
        rememberBackupImportLauncher { json ->
            if (json != null) {
                viewModel.onAction(ProfileAction.OnImportDataSelected(json))
            }
        }

    DisposableEffect(viewModel) {
        appState.onProfileSettingsClick = {
            viewModel.onAction(ProfileAction.OnSettingsClick)
        }
        onDispose {
            appState.onProfileSettingsClick = null
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ProfileEvent.ShowSnackbar -> {
                snackbarMessage = event.message
            }

            is ProfileEvent.RequestNotificationPermission -> {
                when (event.target) {
                    NotificationPermissionTarget.DailyBeyt -> requestNotificationPermission()
                    NotificationPermissionTarget.Remote -> showRemotePermissionSheet = true
                }
            }

            ProfileEvent.OpenAppNotificationSettings -> {
                openAppNotificationSettings()
            }
        }
    }

    snackbarMessage?.let { message ->
        val resolvedMessage = message.asString()
        LaunchedEffect(resolvedMessage) {
            showToast(resolvedMessage)
            snackbarMessage = null
        }
    }

    BaseScreen(
        screenState = state.screenState,
        onRetry = { viewModel.onAction(ProfileAction.OnRetryClick) },
    ) {
        ProfileScreen(
            state = state,
            onAction = viewModel::onAction,
        )
        ProfileSheets(
            state = state,
            onAction = viewModel::onAction,
            onExportData = { viewModel.onAction(ProfileAction.OnExportData) },
            onImportData = openImportPicker,
        )
    }

    if (state.pendingImportJson != null) {
        AzbarkonAlertDialog(
            onDismissRequest = { viewModel.onAction(ProfileAction.OnCancelImport) },
            title = stringResource(Res.string.profile_import_confirm_title),
            text = stringResource(Res.string.profile_import_confirm_body),
            confirmLabel = stringResource(Res.string.clear_confirm),
            onConfirm = { viewModel.onAction(ProfileAction.OnConfirmImport) },
            dismissLabel = stringResource(Res.string.clear_cancel),
        )
    }

    if (showRemotePermissionSheet) {
        NotificationPermissionSheet(
            onDismiss = { showRemotePermissionSheet = false },
            onResult = { granted ->
                viewModel.onAction(
                    ProfileAction.OnNotificationPermissionResult(
                        granted,
                        NotificationPermissionTarget.Remote,
                    ),
                )
                showRemotePermissionSheet = false
            },
        )
    }
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ProfileHeader(
                levelProgress = state.levelProgress,
                onLevelsClick = { onAction(ProfileAction.OnLevelsIconClick) },
            )
        }

        item {
            GameStatusCard(stats = state.gameStats)
        }

        item {
            MemorizationStatusCard(stats = state.memorizationStats)
        }

        item {
            ProfileBadges(
                badges = state.previewBadges,
                onViewAllClick = { onAction(ProfileAction.OnViewAllBadgesClick) },
            )
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    AzbarkonTheme {
        ProfileScreen(
            state = ProfileState(),
            onAction = {},
        )
    }
}
