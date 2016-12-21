/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.samples.apps.topeka.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.samples.apps.topeka.model.Avatar;
import com.google.samples.apps.topeka.model.MyPlayer;
import com.google.samples.apps.topeka.model.Player;

/**
 * Easy storage and retrieval of preferences.
 */
public class NewPreferencesHelper {

    private static final String PLAYER_PREFERENCES = "playerPreferences";
    private static final String PREFERENCE_NAME = PLAYER_PREFERENCES + ".name";
    private static final String PREFERENCE_EMAIL = PLAYER_PREFERENCES + ".email";
    private NewPreferencesHelper() {
        //no instance
    }

    /**
     * Writes a {@link com.google.samples.apps.topeka.model.Player} to preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @param player The {@link com.google.samples.apps.topeka.model.Player} to write.
     */
    public static void writeToPreferences(Context context, MyPlayer player) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREFERENCE_NAME, player.getName());
        editor.putString(PREFERENCE_EMAIL, player.getEmail());
        editor.apply();
    }

    /**
     * Retrieves a {@link com.google.samples.apps.topeka.model.Player} from preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return A previously saved player or <code>null</code> if none was saved previously.
     */
    public static MyPlayer getPlayer(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        final String name = preferences.getString(PREFERENCE_NAME, null);
        final String email = preferences.getString(PREFERENCE_EMAIL, null);
        if (null == name && null == email) {
            return null;
        }
        return new MyPlayer(name, email);
    }

    /**
     * Signs out a player by removing all it's data.
     *
     * @param context The context which to obtain the SharedPreferences from.
     */
    public static void signOut(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(PREFERENCE_NAME);
        editor.remove(PREFERENCE_EMAIL);
        editor.apply();
    }

    /**
     * Check whether a user is currently signed in.
     *
     * @param context The context to check this in.
     * @return <code>true</code> if login data exists, else <code>false</code>.
     */
    public static boolean isSignedIn(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFERENCE_NAME) &&
                preferences.contains(PREFERENCE_EMAIL);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PLAYER_PREFERENCES, Context.MODE_PRIVATE);
    }
}
