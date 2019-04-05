package model;

import exceptions.PseudoInvalide;

public class Partie {

    private String pseudo;
    private int score;


    public Partie(String pseudo) throws PseudoInvalide {
        if (pseudo.isEmpty() || pseudo == null) {
        	throw new PseudoInvalide();
        }

    }

}
