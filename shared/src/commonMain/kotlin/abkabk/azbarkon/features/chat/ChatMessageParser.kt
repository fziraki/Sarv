package abkabk.azbarkon.features.chat

val PERSIAN_SCRIPT_RANGE = 0x0600..0x06FF

fun extractLastPersianLetter(message: String): Char? {
    return message.filter { it.isLetter() }.lastOrNull { it.code in PERSIAN_SCRIPT_RANGE }
}
