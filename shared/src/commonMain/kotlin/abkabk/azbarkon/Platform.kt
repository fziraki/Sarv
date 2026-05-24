package abkabk.azbarkon

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform