package com.ultimatelinemanager.bean;

import com.formation.utils.LogUtils;
import com.ultimatelinemanager.metier.exception.TechnicalException;

/**
 * Created by Anthony on 06/04/2015.
 */
public enum Role {
    Handler, Middle, Both;

    public static Role getRole(String role) {
        Role role1 = Role.valueOf(role);
        if (role1 == null) {
            LogUtils.logException(Role.class, new TechnicalException("Le role transmit n'existe pas = " + role), true);
            return Role.Both;
        }
        else {
            return role1;
        }

    }
}
