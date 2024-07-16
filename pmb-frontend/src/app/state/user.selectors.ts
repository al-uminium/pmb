// src/app/store/user.selectors.ts
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AuthState, UserState } from './user.state';

export const selectUserState = createFeatureSelector<UserState>('user');
export const selectAuthState = createFeatureSelector<AuthState>('auth');

export const selectSelectedUser = createSelector(
  selectUserState,
  (state: UserState) => state.user
)

export const selectAuthUser = createSelector(
  selectAuthState,
  (state: AuthState) => state.user
)

export const selectIsLogin = createSelector(
  selectAuthState,
  (state: AuthState) => state.isLogin
)