package abkabk.azbarkon.features.chat

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
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
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.chat_persian_only
import sarv.shared.generated.resources.poem_copied
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
    fun `non persian input shows snackbar and does not send`() =
        runTest {
            val viewModel =
                createViewModel(
                    replyDelayMillis = 0L,
                )

            viewModel.onAction(ChatAction.OnInputChange("hello"))
            viewModel.onAction(ChatAction.OnSendClick)
            advanceUntilIdle()

            assertThat(viewModel.state.value.messages).hasSize(0)
            assertThat(viewModel.state.value.isPoetTyping).isFalse()
            assertThat(viewModel.state.value.inputText).isEqualTo("hello")
            val error = viewModel.state.value.screenState as UiScreenState.Error
            assertThat(error.message).isEqualTo(UiText.Resource(Res.string.chat_persian_only))
            assertThat(error.isSuccess).isFalse()
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

            viewModel.onAction(ChatAction.OnPoetMessageLongPress(poetMessage.id))

            assertThat(clipboardService.lastCopiedText).isEqualTo(poetMessage.text)
            val error = viewModel.state.value.screenState as UiScreenState.Error
            assertThat(error.message).isEqualTo(UiText.Resource(Res.string.poem_copied))
            assertThat(error.isSuccess).isTrue()
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
