package com.formation.utils;

import android.util.Log;

/**
 * @author Anthony
 *         <p/>
 *         Bonne pratique de log :
 *         <p/>
 *         if (UPConst.LOG_DEV_MODE) {
 *         log.d(TAG, "message");
 *         }
 *         Permet à la compilation en prod de supprimer tous les instructions de log
 * @since 29.09.11 6:38
 */
public class LogUtils {

    //permet de voir le nom de l'activité en cours
    //qui utilise le tag debug
    public static final String SET_DEBUG_LOGTAG = "SET_DEBUG_LOGTAG";
    //Message des exceptionA
    public static final String SET_EXCEPTION_MESSAGE = "SET_EXCEPTION_MESSAGE";
    public static final String SET_CURRENT_SCREEN = "SET_CURRENT_SCREEN";
    public static final String SET_BLUETOOTH_INFORMATION = "SET_BLUETOOTH_INFORMATION";
    public static final String SET_SERVER_INFORMATION = "SET_SERVER_INFORMATION";
    public static final String SET_MESSAGE_ECHANGE = "SET_MESSAGE_ECHANGE";

    private static final String PREFIX = "MPosClientAndroid-";

    public static void logCurrentScreen(String log) {
        Log.d(SET_CURRENT_SCREEN, log);
    }

    public static String getLogTag(final Class<?> clazz) {
        return PREFIX + clazz.getSimpleName();
    }

    public static void logMessage(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            if (SET_BLUETOOTH_INFORMATION.equals(TAG)) {
                Log.w(TAG, message);
            }
            else {
                Log.d(TAG, message);
            }

        }
    }

    /**
     * Methode outil de debugg permettant un log temporaire rapide. Mais supprimer les appels une fois terminée.
     * Permet de voir les appels avec le tag DEBUG dans logcat
     * Open Call hierarchie ne doit rien renvoyer
     */
    public static void logTemp(final String log) {
        Log.w(SET_DEBUG_LOGTAG, log);
    }

    public static void logException(final String TAG, final Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * Affiche dans les logs les messages des exceptions
     *
     * @param message
     */
    public static void logMessageException(String message) {
        if (BuildConfig.DEBUG) {
            Log.w(SET_EXCEPTION_MESSAGE, message);
        }
    }

}
