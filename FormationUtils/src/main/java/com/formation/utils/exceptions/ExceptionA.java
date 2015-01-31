package com.formation.utils.exceptions;

/**
 * Created by amonteiro on 17/09/2014.
 */
public class ExceptionA extends Exception {

    private String exceptionName;
    private String messageUtilisateur, messageTechnique;
    private int codeErreur;

    //-----------------------
    // Constructeur
    //------------------------

    /**
     * @param messageUtilisateur message que recevra l'utilisateur
     * @param messageTechnique   message pour les logs
     */
    public ExceptionA(String exceptionName, String messageUtilisateur, String messageTechnique) {
        super();
        this.messageUtilisateur = messageUtilisateur;
        this.messageTechnique = messageTechnique;
        this.exceptionName = exceptionName;
    }

    /**
     * @param messageUtilisateur message que recevra l'utilisateur
     * @param messageTechnique   message pour les logs
     * @param throwable
     */
    public ExceptionA(String exceptionName, String messageUtilisateur, String messageTechnique, Throwable throwable) {
        super(throwable);
        this.messageUtilisateur = messageUtilisateur;
        this.messageTechnique = messageTechnique;
        this.exceptionName = exceptionName;
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

    public String getExceptionName() {
        return exceptionName;
    }

    public int getCodeErreur() {
        return codeErreur;
    }

    public void setCodeErreur(int codeErreur) {
        this.codeErreur = codeErreur;
    }
}
