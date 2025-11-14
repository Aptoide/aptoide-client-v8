package cm.aptoide.pt.play_and_earn.sessions.data

class DuplicateOrOutOfOrderException(message: String) : Exception(message)

class SessionExpiredException(message: String) : Exception(message)

class AlreadyEndedException(message: String) : Exception(message)
