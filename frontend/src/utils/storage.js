const TOKEN_KEY = 'campus-snack-mall:token'
const USER_KEY = 'campus-snack-mall:user'

export function saveAuthSession(token, userInfo) {
  sessionStorage.setItem(TOKEN_KEY, token ?? '')
  sessionStorage.setItem(USER_KEY, JSON.stringify(userInfo ?? null))
}

export function clearAuthSession() {
  sessionStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(USER_KEY)
}

export function loadAuthSession() {
  const token = sessionStorage.getItem(TOKEN_KEY)
  const userRaw = sessionStorage.getItem(USER_KEY)

  return {
    token: token || '',
    userInfo: userRaw ? JSON.parse(userRaw) : null,
  }
}
