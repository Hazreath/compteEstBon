package exceptions;

public class AnnulerException extends Exception {

	public AnnulerException() {
		super("Pas d'opération à annuler.");
	}
}
