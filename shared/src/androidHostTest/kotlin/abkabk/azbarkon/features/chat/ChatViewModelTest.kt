package abkabk.azbarkon.features.chat

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithCategories
import abkabk.azbarkon.testing.FakeChatRepository
import abkabk.azbarkon.testing.FakeClipboardService
import abkabk.azbarkon.testing.FakePoetRepository
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.poem_copied
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads poet header on start`() =
        runTest {
            val viewModel = createViewModel()

            val state = viewModel.state.value
            assertThat(state.screenState).isInstanceOf(UiScreenState.Success::class)
            assertThat(state.poetName).isEqualTo("حافظ شیرازی")
        }

    @Test
    fun `ignores empty send`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.onAction(ChatAction.OnSendClick)

            assertThat(viewModel.state.value.messages).hasSize(0)
            assertThat(viewModel.state.value.isPoetTyping).isFalse()
        }

    @Test
    fun `send adds user message typing flag and poet reply`() =
        runTest {
            val chatRepository = FakeChatRepository()
            val viewModel =
                createViewModel(
                    chatRepository = chatRepository,
                    replyDelayMillis = 0L,
                )

            viewModel.onAction(ChatAction.OnInputChange("دلم گرفته"))
            viewModel.onAction(ChatAction.OnSendClick)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertThat(state.inputText).isEqualTo("")
            assertThat(state.isPoetTyping).isFalse()
            assertThat(state.messages).hasSize(2)
            assertThat(state.messages.first().isFromUser).isTrue()
            assertThat(state.messages.first().text).isEqualTo("دلم گرفته")
            assertThat(state.messages.last().isFromUser).isFalse()
            assertThat(chatRepository.lastPoetId).isEqualTo(2)
            assertThat(chatRepository.lastLetter).isEqualTo('ه')
        }

    @Test
    fun `invalid message gets emoji fallback`() =
        runTest {
            val viewModel =
                createViewModel(
                    replyDelayMillis = 0L,
                )

            viewModel.onAction(ChatAction.OnInputChange("???"))
            viewModel.onAction(ChatAction.OnSendClick)
            advanceUntilIdle()

            val poetMessage = viewModel.state.value.messages.last()
            assertThat(poetMessage.isFromUser).isFalse()
            assertThat(poetMessage.text).isEqualTo("😐\n\n???")
        }

    @Test
    fun `long press copies poet message and shows snackbar`() =
        runTest {
            val clipboardService = FakeClipboardService()
            val viewModel =
                createViewModel(
                    clipboardService = clipboardService,
                    replyDelayMillis = 0L,
                )

            viewModel.onAction(ChatAction.OnInputChange("دلم گرفته"))
            viewModel.onAction(ChatAction.OnSendClick)
            advanceUntilIdle()

            val poetMessage = viewModel.state.value.messages.last()

            viewModel.events.test {
                viewModel.onAction(ChatAction.OnPoetMessageLongPress(poetMessage.id))

                assertThat(clipboardService.lastCopiedText).isEqualTo(poetMessage.text)
                assertThat(awaitItem()).isEqualTo(
                    ChatEvent.ShowSnackbar(
                        UiText.Resource(Res.string.poem_copied),
                    ),
                )
            }
        }

    private fun createViewModel(
        chatRepository: FakeChatRepository = FakeChatRepository(),
        clipboardService: FakeClipboardService = FakeClipboardService(),
        replyDelayMillis: Long = 0L,
    ): ChatViewModel {
        val poetRepository =
            FakePoetRepository().apply {
                poetsWithCategories =
                    listOf(
                        PoetWithCategories(
                            poet =
                                Poet(
                                    id = 2,
                                    name = "حافظ شیرازی",
                                    description = "بیو",
                                    rootCatId = 9,
                                    imageUrl = null,
                                ),
                            categories = emptyList(),
                        ),
                    )
            }

        return ChatViewModel(
            poetRepository = poetRepository,
            chatRepository = chatRepository,
            clipboardService = clipboardService,
            poetId = 2,
            replyDelayMillis = replyDelayMillis,
        )
    }
}
