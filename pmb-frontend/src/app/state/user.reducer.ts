import { Action, createReducer, on } from "@ngrx/store";
import { User } from "../classes/user";
import { loginUser, logoutUser, selectUser } from "./user.actions";
import { AuthState, initialAuthState, initialUserState, UserState } from "./user.state";

export const _authReducer = createReducer(
  initialAuthState,
  on(loginUser, (state, { user }) => ({
    ...state, 
    user,
    isLogin : true
  })),
  on(logoutUser, state => ({ 
    ...state, 
    user: null, 
    isLogin: false}))
)

export const _userReducer = createReducer(
  initialUserState,
  on(selectUser, (state, { user }) => ({
    ...state,
    user
  })),
);

export function authReducer(state: AuthState | undefined, action: Action) {
  return _authReducer(state, action)
}

export function userReducer(state: UserState | undefined, action: Action) {
  return _userReducer(state, action)
}