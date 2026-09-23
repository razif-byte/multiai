package com.example.data.auth

import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class AuthManager(
    private val userDao: UserDao,
    private val scope: CoroutineScope
) {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        scope.launch(Dispatchers.IO) {
            userDao.getActiveUserFlow().collect { user ->
                _currentUser.value = user
            }
        }
    }

    suspend fun signUp(username: String, email: String, password: String): Boolean {
        _authError.value = null
        val cleanEmail = email.trim().lowercase()
        val cleanUsername = username.trim()

        if (cleanUsername.isBlank()) {
            _authError.value = "Nama pengguna tidak boleh kosong."
            return false
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            _authError.value = "Emel tidak sah."
            return false
        }
        if (password.length < 6) {
            _authError.value = "Kata laluan mestilah sekurang-kurangnya 6 aksara."
            return false
        }

        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            _authError.value = "Akaun dengan emel ini sudah wujud."
            return false
        }

        userDao.setAllLoggedOut()
        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            username = cleanUsername,
            email = cleanEmail,
            passwordHash = hashPassword(password),
            isLoggedIn = true
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        return true
    }

    suspend fun login(email: String, password: String): Boolean {
        _authError.value = null
        val cleanEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(cleanEmail)
        if (user == null) {
            _authError.value = "Akaun tidak dijumpai."
            return false
        }

        if (user.passwordHash != hashPassword(password)) {
            _authError.value = "Kata laluan tidak tepat."
            return false
        }

        userDao.setAllLoggedOut()
        userDao.setLoggedIn(user.id)
        _currentUser.value = user.copy(isLoggedIn = true)
        return true
    }

    suspend fun loginAsGuest(guestName: String = "Pengguna Tetamu"): Boolean {
        _authError.value = null
        val guestEmail = "guest_${System.currentTimeMillis() % 10000}@multiai.nasadef"
        userDao.setAllLoggedOut()
        val guestUser = UserEntity(
            id = UUID.randomUUID().toString(),
            username = guestName,
            email = guestEmail,
            passwordHash = hashPassword("guest123"),
            authProvider = "guest",
            isLoggedIn = true
        )
        userDao.insertUser(guestUser)
        _currentUser.value = guestUser
        return true
    }

    suspend fun loginWithGoogle(emailInput: String = "laptoprazif@gmail.com", displayName: String = "Razif (Google)"): Boolean {
        _authError.value = null
        val cleanEmail = emailInput.trim().lowercase()
        userDao.setAllLoggedOut()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            userDao.setLoggedIn(existing.id)
            _currentUser.value = existing.copy(isLoggedIn = true, authProvider = "google")
            return true
        }

        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            username = displayName,
            email = cleanEmail,
            passwordHash = hashPassword("google_oauth_${cleanEmail}"),
            authProvider = "google",
            isVip = true,
            isLoggedIn = true
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        return true
    }

    suspend fun loginWithNasadef(memberIdOrEmail: String = "nasadef_member@nasadef.com.my", displayName: String = "Ahli Rasmi Nasadef"): Boolean {
        _authError.value = null
        val cleanEmail = if (memberIdOrEmail.contains("@")) {
            memberIdOrEmail.trim().lowercase()
        } else {
            "${memberIdOrEmail.trim().lowercase()}@nasadef.com.my"
        }

        userDao.setAllLoggedOut()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            userDao.setLoggedIn(existing.id)
            _currentUser.value = existing.copy(isLoggedIn = true, authProvider = "nasadef", isVip = true)
            return true
        }

        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            username = displayName,
            email = cleanEmail,
            passwordHash = hashPassword("nasadef_sso_${cleanEmail}"),
            authProvider = "nasadef",
            isVip = true,
            isLoggedIn = true
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        return true
    }

    suspend fun loginWithMicrosoft(emailInput: String = "user@outlook.com", displayName: String = "Pengguna Microsoft"): Boolean {
        _authError.value = null
        val cleanEmail = emailInput.trim().lowercase()
        userDao.setAllLoggedOut()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            userDao.setLoggedIn(existing.id)
            _currentUser.value = existing.copy(isLoggedIn = true, authProvider = "microsoft")
            return true
        }

        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            username = displayName,
            email = cleanEmail,
            passwordHash = hashPassword("microsoft_oauth_${cleanEmail}"),
            authProvider = "microsoft",
            isVip = true,
            isLoggedIn = true
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        return true
    }

    suspend fun logout() {
        userDao.setAllLoggedOut()
        _currentUser.value = null
    }

    suspend fun updatePreferences(modelId: String, langCode: String) {
        val user = _currentUser.value ?: return
        userDao.updatePreferredModel(user.id, modelId)
        userDao.updatePreferredLanguage(user.id, langCode)
        _currentUser.value = user.copy(
            preferredModelId = modelId,
            preferredLanguageCode = langCode
        )
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
