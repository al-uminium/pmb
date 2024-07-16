import { createAction, props } from "@ngrx/store";
import { User } from "../classes/user";

export const selectUser = createAction('[User] Select User', props<{ user: User }>());

export const loginUser = createAction('[Auth Login User', props<{ user: User}>());
export const logoutUser = createAction('[Auth] Logout User');