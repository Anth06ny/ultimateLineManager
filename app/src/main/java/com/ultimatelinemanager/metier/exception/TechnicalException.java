package com.ultimatelinemanager.metier.exception;

import com.ultimatelinemanager.Constante;

/**
 * Exception du à une erreur technique du code, ne doit pas se produire sinon c'est notre faute.
 * Serveur qui ne répond plus
 */
public class TechnicalException extends ExceptionA {

    /**
     * On affichera un message par defaut pour l'utilisateur
     *
     * @param messageTechnique
     */
    public TechnicalException(String messageTechnique) {
        super(Constante.ERREUR_GENERIQUE, messageTechnique);
    }

    /**
     * On affichera un message par defaut pour l'utilisateur
     *
     * @param messageTechnique
     */
    public TechnicalException(String messageTechnique, Throwable throwable) {
        super(Constante.ERREUR_GENERIQUE, messageTechnique, throwable);
    }

}
