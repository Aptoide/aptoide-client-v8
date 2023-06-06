package cm.aptoide.pt.profile.domain

import cm.aptoide.pt.profile.data.UserProfileRepository
import cm.aptoide.pt.profile.data.model.UserProfile
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class UserProfileUseCase @Inject constructor(private val userProfileRepository: UserProfileRepository) {

  suspend fun createUser(user: UserProfile) = userProfileRepository.createUser(user)

  suspend fun setUser(user: UserProfile) = userProfileRepository.setUser(user)

  suspend fun deleteUser() = userProfileRepository.deleteUser()

  fun getUser(): Flow<UserProfile> = userProfileRepository.getUser()
}
