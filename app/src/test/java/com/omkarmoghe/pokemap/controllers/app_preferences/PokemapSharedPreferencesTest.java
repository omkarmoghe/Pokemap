//package com.omkarmoghe.pokemap.controllers.app_preferences;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(PreferenceManager.class)
//public class PokemapSharedPreferencesTest {
//    private static final String USERNAME_KEY = "UsernameKey";
//    private static final String PASSWORD_KEY = "PasswordKey";
//
//    @Mock
//    private Context context;
//
//    @Mock
//    private SharedPreferences sharedPreferences;
//
//    @Mock
//    SharedPreferences.Editor editor;
//
//    private PokemapSharedPreferences systemUnderTesting;
//
//    @Before
//    public void setUp() throws Exception {
//        PowerMockito.mockStatic(PreferenceManager.class);
//        when(PreferenceManager.getDefaultSharedPreferences(context)).thenReturn(sharedPreferences);
//        when(editor.putString(anyString(), anyString())).thenReturn(editor);
//        when(sharedPreferences.edit()).thenReturn(editor);
//
//        systemUnderTesting = new PokemapSharedPreferences(context);
//    }
//
//    @Test
//    public void returnTrueIfUsernameIsSet() {
//        //Pretend username was set previously
//        when(sharedPreferences.contains(USERNAME_KEY)).thenReturn(true);
//
//        //Act
//        boolean isUsernameSet = systemUnderTesting.isUsernameSet();
//
//        //Assert that shared prefs were called
//        verify(sharedPreferences, times(1)).contains(USERNAME_KEY);
//
//        //Assert that username is set
//        assertEquals(isUsernameSet, true);
//    }
//
//    @Test
//    public void returnFalseIfUsernameIsNotSet() {
//        //Pretend username was not set
//        when(sharedPreferences.contains(USERNAME_KEY)).thenReturn(false);
//
//        //Act
//        boolean isUsernameSet = systemUnderTesting.isUsernameSet();
//
//        //Assert that shared prefs were called
//        verify(sharedPreferences, times(1)).contains(USERNAME_KEY);
//
//        //Assert that username is set
//        assertEquals(isUsernameSet, false);
//    }
//
//    @Test
//    public void returnTrueIfPasswordIsSet() {
//        //Pretend username was set previously
//        when(sharedPreferences.contains(PASSWORD_KEY)).thenReturn(true);
//
//        //Act
//        boolean isPasswordSet = systemUnderTesting.isPasswordSet();
//
//        //Assert that shared prefs were called
//        verify(sharedPreferences, times(1)).contains(PASSWORD_KEY);
//
//        //Assert that username is set
//        assertEquals(isPasswordSet, true);
//    }
//
//    @Test
//    public void returnFalseIfPasswordIsNotSet() {
//        //Pretend username was set previously
//        when(sharedPreferences.contains(PASSWORD_KEY)).thenReturn(false);
//
//        //Act
//        boolean isPasswordSet = systemUnderTesting.isPasswordSet();
//
//        //Assert that shared prefs were called
//        verify(sharedPreferences, times(1)).contains(PASSWORD_KEY);
//
//        //Assert that username is set
//        assertEquals(isPasswordSet, false);
//    }
//
//    @Test
//    public void storeUsernameInSharedPreference() {
//        String username = "username";
//
//        //Act
//        systemUnderTesting.setUsername(username);
//
//        //Assert that shared prefs were called
//        verify(editor, times(1)).putString(USERNAME_KEY, username);
//    }
//
//    @Test
//    public void storePasswordInSharedPreference() {
//        String password = "password";
//
//        //Act
//        systemUnderTesting.setPassword(password);
//
//        //Assert that shared prefs were called
//        verify(editor, times(1)).putString(PASSWORD_KEY, password);
//    }
//
//    @Test
//    public void getUsername() {
//        //Pretend username was set previously
//        String usernameStored = "User Name Stored";
//        when(sharedPreferences.getString(eq(USERNAME_KEY), anyString())).thenReturn(usernameStored);
//
//        //Act
//        String returnedValue = systemUnderTesting.getUsername();
//
//        //Assert
//        verify(sharedPreferences, times(1)).getString(USERNAME_KEY, "");
//        assertEquals(returnedValue, usernameStored);
//    }
//
//    @Test
//    public void getPassword() {
//        //Pretend username was set previously
//        String passwordStored = "Password Stored";
//        when(sharedPreferences.getString(eq(PASSWORD_KEY), anyString())).thenReturn(passwordStored);
//
//        //Act
//        String returnedValue = systemUnderTesting.getPassword();
//
//        //Assert
//        verify(sharedPreferences, times(1)).getString(PASSWORD_KEY, "");
//        assertEquals(returnedValue, passwordStored);
//    }
//}