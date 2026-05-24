package abkabk.azbarkon.app.core.network

interface AuthProvider {
    fun getToken(): String?
}