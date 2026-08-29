package abkabk.azbarkon.features.mypoems

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.domain.model.MyPoemSummary
import abkabk.azbarkon.domain.usecase.GetMyPoemsUseCase
import abkabk.azbarkon.testing.FakePoemRepository
import abkabk.azbarkon.testing.FakeSavedPoemRepository
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class MyPoemsViewModelTest {

    @Test
    fun `loads liked and bookmarked poems grouped by poet and category`() =
        runTest {
            val savedPoemRepository =
                FakeSavedPoemRepository().apply {
                    toggleLike(1)
                    toggleLike(2)
                    toggleBookmark(2)
                    toggleBookmark(3)
                }
            val poemRepository =
                FakePoemRepository().apply {
                    poemsByIds =
                        mapOf(
                            1 to
                                MyPoemSummary(
                                    id = 1,
                                    title = "شمارهٔ ۱",
                                    poetName = "حافظ",
                                    categoryName = "غزلیات",
                                ),
                            2 to
                                MyPoemSummary(
                                    id = 2,
                                    title = "شمارهٔ ۲",
                                    poetName = "حافظ",
                                    categoryName = "غزلیات",
                                ),
                            3 to
                                MyPoemSummary(
                                    id = 3,
                                    title = "شمارهٔ ۳",
                                    poetName = "سعدی",
                                    categoryName = "بوستان",
                                ),
                        )
                }

            val viewModel =
                MyPoemsViewModel(
                    savedPoemRepository = savedPoemRepository,
                    getMyPoems = GetMyPoemsUseCase(poemRepository, savedPoemRepository),
                )

            val state = viewModel.state.value
            assertThat(state.screenState).isInstanceOf(UiScreenState.Success::class)
            assertThat(state.likedGroups).isEqualTo(
                listOf(
                    PoetGroupUi(
                        poetName = "حافظ",
                        categories =
                            listOf(
                                CategoryGroupUi(
                                    categoryName = "غزلیات",
                                    poems =
                                        listOf(
                                            MyPoemItemUi(id = 1, title = "شمارهٔ ۱"),
                                            MyPoemItemUi(id = 2, title = "شمارهٔ ۲"),
                                        ),
                                ),
                            ),
                    ),
                ),
            )
            assertThat(state.bookmarkedGroups).isEqualTo(
                listOf(
                    PoetGroupUi(
                        poetName = "حافظ",
                        categories =
                            listOf(
                                CategoryGroupUi(
                                    categoryName = "غزلیات",
                                    poems = listOf(MyPoemItemUi(id = 2, title = "شمارهٔ ۲")),
                                ),
                            ),
                    ),
                    PoetGroupUi(
                        poetName = "سعدی",
                        categories =
                            listOf(
                                CategoryGroupUi(
                                    categoryName = "بوستان",
                                    poems = listOf(MyPoemItemUi(id = 3, title = "شمارهٔ ۳")),
                                ),
                            ),
                    ),
                ),
            )
        }

    @Test
    fun `tab switch updates active groups`() =
        runTest {
            val savedPoemRepository =
                FakeSavedPoemRepository().apply {
                    toggleLike(1)
                    toggleBookmark(3)
                }
            val poemRepository =
                FakePoemRepository().apply {
                    poemsByIds =
                        mapOf(
                            1 to
                                MyPoemSummary(
                                    id = 1,
                                    title = "شمارهٔ ۱",
                                    poetName = "حافظ",
                                    categoryName = "غزلیات",
                                ),
                            3 to
                                MyPoemSummary(
                                    id = 3,
                                    title = "شمارهٔ ۳",
                                    poetName = "سعدی",
                                    categoryName = "بوستان",
                                ),
                        )
                }

            val viewModel =
                MyPoemsViewModel(
                    savedPoemRepository = savedPoemRepository,
                    getMyPoems = GetMyPoemsUseCase(poemRepository, savedPoemRepository),
                )

            viewModel.onAction(MyPoemsAction.OnTabSelected(MyPoemsTab.Bookmarked))

            val state = viewModel.state.value
            assertThat(state.selectedTab).isEqualTo(MyPoemsTab.Bookmarked)
            assertThat(state.activeGroups.single().poetName).isEqualTo("سعدی")
        }

    @Test
    fun `remove poem updates active tab list`() =
        runTest {
            val savedPoemRepository =
                FakeSavedPoemRepository().apply {
                    toggleLike(1)
                    toggleLike(2)
                }
            val poemRepository =
                FakePoemRepository().apply {
                    poemsByIds =
                        mapOf(
                            1 to
                                MyPoemSummary(
                                    id = 1,
                                    title = "شمارهٔ ۱",
                                    poetName = "حافظ",
                                    categoryName = "غزلیات",
                                ),
                            2 to
                                MyPoemSummary(
                                    id = 2,
                                    title = "شمارهٔ ۲",
                                    poetName = "حافظ",
                                    categoryName = "غزلیات",
                                ),
                        )
                }

            val viewModel =
                MyPoemsViewModel(
                    savedPoemRepository = savedPoemRepository,
                    getMyPoems = GetMyPoemsUseCase(poemRepository, savedPoemRepository),
                )

            viewModel.onAction(MyPoemsAction.OnRemovePoem(poemId = 1))

            val state = viewModel.state.value
            assertThat(state.likedGroups.single().categories.single().poems).isEqualTo(
                listOf(MyPoemItemUi(id = 2, title = "شمارهٔ ۲")),
            )
        }

    @Test
    fun `clear all empties only active tab`() =
        runTest {
            val savedPoemRepository =
                FakeSavedPoemRepository().apply {
                    toggleLike(1)
                    toggleBookmark(3)
                }
            val poemRepository =
                FakePoemRepository().apply {
                    poemsByIds =
                        mapOf(
                            1 to
                                MyPoemSummary(
                                    id = 1,
                                    title = "شمارهٔ ۱",
                                    poetName = "حافظ",
                                    categoryName = "غزلیات",
                                ),
                            3 to
                                MyPoemSummary(
                                    id = 3,
                                    title = "شمارهٔ ۳",
                                    poetName = "سعدی",
                                    categoryName = "بوستان",
                                ),
                        )
                }

            val viewModel =
                MyPoemsViewModel(
                    savedPoemRepository = savedPoemRepository,
                    getMyPoems = GetMyPoemsUseCase(poemRepository, savedPoemRepository),
                )

            viewModel.onAction(MyPoemsAction.OnClearAllConfirm)

            val state = viewModel.state.value
            assertThat(state.likedGroups).isEmpty()
            assertThat(state.bookmarkedGroups.single().poetName).isEqualTo("سعدی")
        }

    @Test
    fun `poem click emits navigation event`() =
        runTest {
            val fakePoemRepo = FakePoemRepository()
            val fakeSavedRepo = FakeSavedPoemRepository()
            val viewModel =
                MyPoemsViewModel(
                    savedPoemRepository = fakeSavedRepo,
                    getMyPoems = GetMyPoemsUseCase(fakePoemRepo, fakeSavedRepo),
                )

            viewModel.events.test {
                viewModel.onAction(MyPoemsAction.OnPoemClick(poemId = 42))

                val event = awaitItem()
                assertThat(event).isInstanceOf<MyPoemsEvent.NavigateToPoemDetail>()
                assertThat((event as MyPoemsEvent.NavigateToPoemDetail).poemId).isEqualTo(42)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `clear all click shows dialog only when active tab has items`() =
        runTest {
            val fakePoemRepo = FakePoemRepository()
            val fakeSavedRepo = FakeSavedPoemRepository()
            val viewModel =
                MyPoemsViewModel(
                    savedPoemRepository = fakeSavedRepo,
                    getMyPoems = GetMyPoemsUseCase(fakePoemRepo, fakeSavedRepo),
                )

            viewModel.onAction(MyPoemsAction.OnClearAllClick)

            assertThat(viewModel.state.value.showClearDialog).isFalse()
        }

    @Test
    fun `clear all click opens dialog when active tab has items`() =
        runTest {
            val savedPoemRepository =
                FakeSavedPoemRepository().apply {
                    toggleLike(1)
                }
            val poemRepository =
                FakePoemRepository().apply {
                    poemsByIds =
                        mapOf(
                            1 to
                                MyPoemSummary(
                                    id = 1,
                                    title = "شمارهٔ ۱",
                                    poetName = "حافظ",
                                    categoryName = "غزلیات",
                                ),
                        )
                }

            val viewModel =
                MyPoemsViewModel(
                    savedPoemRepository = savedPoemRepository,
                    getMyPoems = GetMyPoemsUseCase(poemRepository, savedPoemRepository),
                )

            viewModel.onAction(MyPoemsAction.OnClearAllClick)

            assertThat(viewModel.state.value.showClearDialog).isTrue()
        }
}
