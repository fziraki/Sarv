package abkabk.azbarkon.core.network

interface AuthProvider {
    fun getToken(): String?
}
