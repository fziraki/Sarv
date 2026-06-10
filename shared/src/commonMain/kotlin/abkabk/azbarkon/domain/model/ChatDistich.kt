package abkabk.azbarkon.domain.model

data class ChatDistich(
    val poemId: Int,
    val rightText: String,
    val leftText: String,
)

object ChatDistichFallback {
    const val POEM_ID = 3065

    const val RIGHT_TEXT = "به قول مولانا : \n\nگفتند یافت می نشود جسته ایم ما"

    const val LEFT_TEXT = "گفت آنک یافت می نشود آنم آرزوست!"
}
