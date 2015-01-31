package com.formation.utils.exceptions;

/**
 * Exception gerant un problème du à l'utilisateur, style mdp faux, date non rentrée...
 */
public class LogicException extends ExceptionA {

    private static final String exceptionName = LogicException.class.getName();

    /**
     * On n'affiche qu'un message pour l'utilisateur
     *
     * @param messageUtilisateur
     */
    public LogicException(String messageUtilisateur) {
        super(exceptionName, messageUtilisateur, null);
    }

    /**
     * On n'affiche qu'un message pour l'utilisateur
     *
     */
    public LogicException(String messageUtilisateur, Throwable e) {
        super(exceptionName, messageUtilisateur, null, e);
    }
}
