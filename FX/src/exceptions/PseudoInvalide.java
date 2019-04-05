/**
 *
 */
package exceptions;

/**
 * @author b.camp
 *
 */
public class PseudoInvalide extends Exception {
	private static final String msg = "Erreur : merci de renseigner votre pseudo.";

	public PseudoInvalide() {
		super(msg);
	}
}
