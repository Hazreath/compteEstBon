package model;

import application.Controller;
import exceptions.OperationInvalide;

public class Operation {

    public static int NB_COMPOSANTES = 3;

    private String opStr;
    private int resultat;
    private int[] operandes = new int[Controller.NB_OPERANDES];


    public Operation(String op) throws OperationInvalide {
    	System.out.println(verifFormat(op));
    	this.opStr = op;

        if (!verifFormat(op)) {
        	throw new OperationInvalide();

        }
    }

    private boolean verifFormat(String line) {
        String[] lineSplit = line.split(" ");

        if (lineSplit.length != NB_COMPOSANTES || line == null || line.isEmpty()) {
            return false;
        }

        // SALE
        char operation = lineSplit[1].toCharArray()[0];
        this.operandes[0] = Integer.parseInt(lineSplit[0]);
        this.operandes[1] = Integer.parseInt(lineSplit[2]);
        int result = 0;


        switch (operation) {
            case '+' : this.resultat = this.operandes[0] + this.operandes[1];
                       break;
            case '-' : this.resultat = this.operandes[0] - this.operandes[1];
                          break;
            case 'x' : this.resultat = this.operandes[0] * this.operandes[1];
                          break;
            case '/' : this.resultat = this.operandes[0] / this.operandes[1];
                          break;
            default:
            	return false; // l'opération n'est pas référencée
        }

        this.opStr += " = " + this.resultat;

        return true;
    }

    private void display(String[] tab) {
        for (String e : tab) {
            System.out.print(e + " ");
        }
    }
    public static void main(String[] args) throws Exception {
//    	Operation lel = new Operation("op");
        Operation ok = new Operation("1 + 87");
        System.out.println(ok.toString());
        Operation okF = new Operation("15 + 10");
        System.out.println(okF.toString());

    }

    @Override
    public String toString() {
    	return this.opStr;
    }

    public int getResultat() {
    	return this.resultat;
    }

    public int[] getOperandes() {
    	return this.operandes;
    }
}
