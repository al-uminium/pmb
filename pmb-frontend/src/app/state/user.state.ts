import { User } from "../classes/user"

export interface UserState {
  user: User | null
}

export interface AuthState {
  user: User | null
  isLogin: boolean
}

export interface AppUserState {
  auth: AuthState;
  selectedUser: UserState
}

export const initialUserState: UserState = {
  user: null
}

export const initialAuthState: AuthState = {
  user: null,
  isLogin: false
}