package com.ultimatelinemanager.metier.exception;

import com.ultimatelinemanager.MyApplication;

/**
 * Exception gerant un problème du à l'utilisateur, style mdp faux, date non rentrée...
 */
public class LogicException extends ExceptionA {

    public LogicException(int messageUtilisateurId) {
        super(MyApplication.getInstance().getApplicationContext().getString(messageUtilisateurId), null);
    }

    /**
     * On n'affiche qu'un message pour l'utilisateur
     *
     * @param messageUtilisateur
     */
    public LogicException(String messageUtilisateur) {
        super(messageUtilisateur, null);
    }

    /**
     * On n'affiche qu'un message pour l'utilisateur
     *
     */
    public LogicException(String messageUtilisateur, Throwable e) {
        super(messageUtilisateur, null, e);
    }

    /**
     * On n'affiche qu'un message pour l'utilisateur
     *
     */
    public LogicException(int messageUtilisateurId, Throwable e) {
        super(MyApplication.getInstance().getApplicationContext().getString(messageUtilisateurId), null, e);
    }
}
