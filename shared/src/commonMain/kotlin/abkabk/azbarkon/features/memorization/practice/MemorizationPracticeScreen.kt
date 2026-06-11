package abkabk.azbarkon.features.memorization.practice

import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.core.ui_base.LocalAzbarkonAppState
import abkabk.azbarkon.core.ui_base.ObserveAsEvents
import abkabk.azbarkon.core.ui_base.asString
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import abkabk.azbarkon.domain.srs.DiffTokenType
import abkabk.azbarkon.ui.components.AzbarkonButton
import abkabk.azbarkon.ui.components.AzbarkonPrimaryButton
import abkabk.azbarkon.ui.components.AzbarkonSecondaryButton
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.memorization_grade_again
import azbarkoncmp.shared.generated.resources.memorization_grade_easy
import azbarkoncmp.shared.generated.resources.memorization_button
import azbarkoncmp.shared.generated.resources.memorization_grade_good
import azbarkoncmp.shared.generated.resources.memorization_grade_hard
import azbarkoncmp.shared.generated.resources.memorization_practice_complete
import azbarkoncmp.shared.generated.resources.memorization_practice_done
import azbarkoncmp.shared.generated.resources.memorization_practice_progress
import azbarkoncmp.shared.generated.resources.memorization_practice_title
import azbarkoncmp.shared.generated.resources.memorization_reveal_hint
import azbarkoncmp.shared.generated.resources.memorization_submit_typing
import azbarkoncmp.shared.generated.resources.memorization_typing_hint
import azbarkoncmp.shared.generated.resources.memorization_typing_mode
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MemorizationPracticeRoot(
    poemId: Int?,
    onBackClick: () -> Unit,
    viewModel: MemorizationPracticeViewModel = koinViewModel { parametersOf(poemId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<abkabk.azbarkon.core.ui_base.UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            MemorizationPracticeEvent.NavigateBack -> onBackClick()
            is MemorizationPracticeEvent.ShowSnackbar -> snackbarMessage = event.message
        }
    }

    snackbarMessage?.let { message ->
        val resolvedMessage = message.asString()
        LaunchedEffect(resolvedMessage) {
            appState.showSnackbar(resolvedMessage)
            snackbarMessage = null
        }
    }

    BaseScreen(
        screenState = state.screenState,
        onRetry = { viewModel.onAction(MemorizationPracticeAction.OnRetryClick) },
    ) {
        MemorizationPracticeScreen(
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
fun MemorizationPracticeScreen(
    state: MemorizationPracticeState,
    onAction: (MemorizationPracticeAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            title = stringResource(Res.string.memorization_practice_title),
            onBackClick = { onAction(MemorizationPracticeAction.OnBackClick) },
        )

        when (state.phase) {
            PracticePhase.COMPLETE -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(Res.string.memorization_practice_complete),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AzbarkonPrimaryButton(
                        text = stringResource(Res.string.memorization_practice_done),
                        onClick = { onAction(MemorizationPracticeAction.OnBackClick) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            else -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    state.currentCard?.let { card ->
                        Text(
                            text =
                                stringResource(
                                    Res.string.memorization_practice_progress,
                                    state.cardIndex,
                                    state.totalCards,
                                ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        AzbarkonSecondaryButton(
                            text = stringResource(Res.string.memorization_typing_mode),
                            onClick = { onAction(MemorizationPracticeAction.OnToggleTypingMode) },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        AnimatedContent(
                            targetState = card.id,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "cardTransition",
                        ) {
                            PracticeCardContent(
                                state = state,
                                card = card,
                                onAction = onAction,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeCardContent(
    state: MemorizationPracticeState,
    card: PracticeCardUi,
    onAction: (MemorizationPracticeAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (state.phase) {
            PracticePhase.SHOW_FRONT -> {
                if (state.isTypingMode) {
                    Text(
                        text = card.front,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    BasicTextField(
                        value = state.typedAnswer,
                        onValueChange = { onAction(MemorizationPracticeAction.OnTypedAnswerChange(it)) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        textStyle =
                            MaterialTheme.typography.bodyLarge.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                        decorationBox = { inner ->
                            if (state.typedAnswer.isEmpty()) {
                                Text(
                                    text = stringResource(Res.string.memorization_typing_hint),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            inner()
                        },
                    )
                    AzbarkonPrimaryButton(
                        text = stringResource(Res.string.memorization_submit_typing),
                        onClick = { onAction(MemorizationPracticeAction.OnSubmitTypedAnswer) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.typedAnswer.isNotBlank(),
                    )
                } else {
                    Text(
                        text = card.front,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { onAction(MemorizationPracticeAction.OnRevealClick) }
                                .padding(vertical = 32.dp),
                    )
                    Text(
                        text = stringResource(Res.string.memorization_reveal_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            PracticePhase.REVEALED -> {
                Text(
                    text = card.back,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                )
                GradeButtons(onAction = onAction)
            }

            PracticePhase.FEEDBACK -> {
                DiffText(
                    tokens = state.diffTokens,
                    fallback = card.back,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                )
                GradeButtons(
                    onAction = onAction,
                    selectedGrade = state.selectedGrade,
                    suggestedGrade = state.suggestedGrade,
                )
                if (state.selectedGrade != null) {
                    AzbarkonPrimaryButton(
                        text = stringResource(Res.string.memorization_button),
                        onClick = { onAction(MemorizationPracticeAction.OnNextCard) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            PracticePhase.COMPLETE -> Unit
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GradeButtons(
    onAction: (MemorizationPracticeAction) -> Unit,
    selectedGrade: SrsGrade? = null,
    suggestedGrade: SrsGrade? = null,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GradeButton(
            label = stringResource(Res.string.memorization_grade_again),
            grade = SrsGrade.AGAIN,
            isSelected = selectedGrade == SrsGrade.AGAIN,
            isSuggested = suggestedGrade == SrsGrade.AGAIN,
            onAction = onAction,
        )
        GradeButton(
            label = stringResource(Res.string.memorization_grade_hard),
            grade = SrsGrade.HARD,
            isSelected = selectedGrade == SrsGrade.HARD,
            isSuggested = suggestedGrade == SrsGrade.HARD,
            onAction = onAction,
        )
        GradeButton(
            label = stringResource(Res.string.memorization_grade_good),
            grade = SrsGrade.GOOD,
            isSelected = selectedGrade == SrsGrade.GOOD,
            isSuggested = suggestedGrade == SrsGrade.GOOD,
            onAction = onAction,
        )
        GradeButton(
            label = stringResource(Res.string.memorization_grade_easy),
            grade = SrsGrade.EASY,
            isSelected = selectedGrade == SrsGrade.EASY,
            isSuggested = suggestedGrade == SrsGrade.EASY,
            onAction = onAction,
        )
    }
}

@Composable
private fun GradeButton(
    label: String,
    grade: SrsGrade,
    isSelected: Boolean,
    isSuggested: Boolean,
    onAction: (MemorizationPracticeAction) -> Unit,
) {
    val containerColor =
        when {
            isSelected -> MaterialTheme.colorScheme.primary
            isSuggested -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    AzbarkonButton(
        text = label,
        onClick = { onAction(MemorizationPracticeAction.OnGradeClick(grade)) },
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    )
}

@Composable
private fun DiffText(
    tokens: List<abkabk.azbarkon.domain.srs.DiffToken>,
    fallback: String,
    modifier: Modifier = Modifier,
) {
    val annotated: AnnotatedString =
        if (tokens.isEmpty()) {
            AnnotatedString(fallback)
        } else {
            buildAnnotatedString {
                tokens.forEachIndexed { index, token ->
                    if (index > 0) append(' ')
                    val color =
                        when (token.type) {
                            DiffTokenType.CORRECT -> Color(0xFF2E7D32)
                            DiffTokenType.MISSING -> Color(0xFFF9A825)
                            DiffTokenType.WRONG -> Color(0xFFC62828)
                        }
                    pushStyle(SpanStyle(color = color))
                    append(token.text)
                    pop()
                }
            }
        }
    Text(
        text = annotated,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun MemorizationPracticeScreenPreview() {
    AzbarkonTheme {
        MemorizationPracticeScreen(
            state =
                MemorizationPracticeState(
                    phase = PracticePhase.SHOW_FRONT,
                    currentCard =
                        PracticeCardUi(
                            id = 1,
                            front = "که عشق آسان نمود اول\n...",
                            back = "که عشق آسان نمود اول\nولی افتاد مشکل‌ها",
                        ),
                    cardIndex = 1,
                    totalCards = 5,
                ),
            onAction = {},
        )
    }
}
