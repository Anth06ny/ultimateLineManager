package com.ultimatelinemanager.metier.exception;

/**
 * Created by amonteiro on 17/09/2014.
 */
public class ExceptionA extends Exception {

    private String messageUtilisateur, messageTechnique;
    private int codeErreur;

    //-----------------------
    // Constructeur
    //------------------------

    /**
     * @param messageUtilisateur message que recevra l'utilisateur
     * @param messageTechnique   message pour les logs
     */
    public ExceptionA(String messageUtilisateur, String messageTechnique) {
        super();
        this.messageUtilisateur = messageUtilisateur;
        this.messageTechnique = messageTechnique;
    }

    /**
     * @param messageUtilisateur message que recevra l'utilisateur
     * @param messageTechnique   message pour les logs
     * @param throwable
     */
    public ExceptionA(String messageUtilisateur, String messageTechnique, Throwable throwable) {
        super(throwable);
        this.messageUtilisateur = messageUtilisateur;
        this.messageTechnique = messageTechnique;
    }

    @Override
    public String toString() {
        String s = super.toString() + "\nMessage utilisateur : " + getMessageUtilisateur();
        s += "\nMessage technique : " + getMessageTechnique();
        return s;
    }

    //-----------------------
    // Getter setter
    //------------------------
    public String getMessageUtilisateur() {
        return messageUtilisateur;
    }

    public String getMessageTechnique() {
        return messageTechnique;
    }

}
