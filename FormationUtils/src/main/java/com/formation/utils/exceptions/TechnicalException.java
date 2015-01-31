package com.formation.utils.exceptions;

/**
 * Exception du à une erreur technique du code, ne doit pas se produire sinon c'est notre faute.
 * Serveur qui ne répond plus
 */
public class TechnicalException extends ExceptionA {

    private static final String exceptionName = TechnicalException.class.getName();

    /**
     * On affichera un message par defaut pour l'utilisateur
     *
     * @param messageTechnique
     */
    public TechnicalException(String messageTechnique) {
        super(exceptionName, "Une erreur technique est intervenue", messageTechnique);
    }

    /**
     * On affichera un message par defaut pour l'utilisateur
     *
     * @param messageTechnique
     */
    public TechnicalException(String messageTechnique, Throwable throwable) {
        super(exceptionName, "Une erreur technique est intervenue", messageTechnique, throwable);
    }

}
