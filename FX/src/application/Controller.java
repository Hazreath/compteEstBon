package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import exceptions.AnnulerException;
import exceptions.OperationInvalide;
import exceptions.PseudoInvalide;
import exceptions.TropNulException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Operation;

import java.util.Arrays;
import java.util.Random;

public class Controller {

    @FXML
    private BorderPane root;

    @FXML
    private Button btn_jouer;
    @FXML
    private TextField txt_pseudo;
    @FXML
    private Button btn_scores;
    @FXML
    private Label label_hour;

    @FXML
    private Button plus, minus, mult, div;

    @FXML
    private HBox hbox_operations;
    @FXML
    private Button[] operations = { plus, minus, mult, div};

    @FXML
    private TextField txt_operation;

    @FXML
    private Button btn_annuler, btn_valider;

    @FXML
    private ButtonBar btn_bar;
    @FXML
    public TextArea txt_console;

    @FXML
    private Button btn_supprimer;
    @FXML
    private Label label_solution;
    @FXML
    private Button btn_proposer;

    @FXML
    private HBox hbox_user;
    @FXML
    private VBox vbox_app;
    @FXML
    private Label label_time;

    /** Constantes */
    private static int BORNE_SUP = 1000;
    private static int NB_NOMBRE = 6;
    private static int NB_OPE = 4;
    public static int NB_OPERANDES = 2;
    private static final boolean DEBUG = true;
    private static final int MAX_PLAQUES = 6;
    private final URL url_scores = getClass().getResource("scores.txt");
    private final File SCORES = new File(url_scores.getPath());
    private String pseudo = "";
    private Timeline time;
    private int min, sec;
    private final int MIN = 4;
    private final int SEC = 20;

    @FXML
    public void initialize() {
    	d("INIT OK");

    	// INITIALISATION DES ELTS

    	// Tirage des nombres et du resultat
//    	label_solution.setText("" + alea());
//
//    	int[] valPlaques = new int[MAX_PLAQUES];
//    	for (int i = 0; i < valPlaques.length; i++) {
//    		valPlaques[i] = alea();
//    	}

    	// Désactivation de tous les widgets sauf la hbox pseudo
    	hideAll(true);
    	hbox_user.setVisible(true);

    	// STUB : Initialisation de la solution et des plaques
    	label_solution.setText("" + 10);
    	int[] valPlaques ={2,4,6,8,2,69};
    	/** INITIALISATION NOMBRES */
    	for (int i : valPlaques) {
    		final Button b = new Button("" + i);
    		b.setId("" + valPlaques);
    		 b.setOnMouseClicked(e -> {
                 b.setVisible(false);

                 txt_operation.appendText(b.getText() + " ");
                 activerNombres(false);
                 activerOperations(true);

             });
    		btn_bar.getButtons().add(b);
    	}

    	/** INITIALISATION OPERATIONS */
    	for (Node ope : hbox_operations.getChildren()) {
    		final Button b = (Button) ope;
    		 b.setOnMouseClicked(e -> {

                 txt_operation.appendText(b.getText() + " ");
                 activerNombres(true);
                 activerOperations(false);
             });
    	}

    	d("FIN INIT");


    }

    @FXML
    public void clickValider() {
    	try {
			Operation op = new Operation(txt_operation.getText());

			// Succès
			console(op.toString());
			Button newPlaque = new Button("" + op.getResultat());
			newPlaque.setId("" + op.getResultat());


			// Recherche des Ids des opérandes de l'opération
			// validée dans btn_bar et suppression
			Button oldOpe;
			int[] operandes = op.getOperandes();
			 for (int i = 0; i < NB_OPERANDES; i++) {
				 oldOpe = rechNombre("" + operandes[i]);

				 if (oldOpe != null) {

					 btn_bar.getButtons().remove(oldOpe);

				 }
			 }

			 // Initialisation de la nouvelle plaque et ajout
			 newPlaque.setOnMouseClicked(e -> {
                 newPlaque.setVisible(false);
                 txt_operation.appendText(newPlaque.getText() + " ");
                 activerNombres(false);
                 activerOperations(true);
             });
			btn_bar.getButtons().add(newPlaque);

			// Reset interface saisie
			clearSaisie();

		} catch (OperationInvalide e) { // erreur format operation
			txt_operation.setText("");
			txt_operation.setStyle("-fx-background-color: #f44242");
			e.printStackTrace();
		}
    }

    @FXML
    public void clickSupprimer() throws AnnulerException {

    	// Réinstanciation et réaffectation des plaques utilisées
    	// dans la derniere operation
    	if (txt_console.getText().isEmpty()) {
    		btn_annuler.setStyle("-fx-background-color: #f44242");
    		throw new AnnulerException();
    	}

    	String[] list_op = txt_console.getText().split("\n");
    	String last_op = list_op[list_op.length - 1].split("=")[0];
    	String resultat = list_op[list_op.length - 1].split("=")[1];

    	btn_annuler.setStyle("");


    	try {
			Operation op = new Operation(last_op);
			int[] list_operandes = op.getOperandes();

			// Restoration des opérandes
			for (int i = 0; i < NB_OPERANDES; i++) {
				final Button b = new Button(list_operandes[i] + "");
				b.setOnMouseClicked(e -> {
	                 b.setVisible(false);

	                 txt_operation.appendText(b.getText() + " ");
	                 activerNombres(false);
	                 activerOperations(true);

	             });
				btn_bar.getButtons().add(b);
			}

			// Suppression du résultat
			btn_bar.getButtons().remove(rechNombre(resultat.trim()));

			// Suppression de la dernière ligne de la console
			String newText = "";
			for (int i = 0; i < list_op.length - 1; i++) {
				newText += list_op[i] + "\n";
			}
			txt_console.setText(newText);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }

    public void clickAnnuler() throws AnnulerException {

    	if (txt_operation.getText().isEmpty()) {
    		setRouge(txt_operation);
    		throw new AnnulerException();
    	}
    	String[] list_operandes = txt_operation.getText().split(" ");
    	Button b;
    	for (int i = 0; i < list_operandes.length; i += 2) { // esquive le signe
    		b = rechNombre(list_operandes[i].trim());
    		b.setVisible(true);
    	}
    	clearTxtOperation();
    	activerNombres(true);
    	activerOperations(false);


    }
    public void clickJouer() throws PseudoInvalide {

    	if (txt_pseudo.getText().isEmpty()) {
    		setRouge(txt_pseudo);
    		throw new PseudoInvalide();
    	}

    	pseudo = txt_pseudo.getText();
    	// affichage du reste de l'interface
    	hideAll(false);

    	// Démarrage du timer
    	timerOn();
    }

    public void timerOn() {
    	time = new Timeline();
    	KeyFrame k = new KeyFrame(Duration.seconds(1),
    			new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						if (sec == 0) {
							sec = 59;
							min --;
						} else {
							sec--;
						}

						if (min == 0 && sec == 0) {
							tempsEcoule();
						}
						label_time.setText(String.format("%02d:%02d",min,sec));
					}


    	});

    	// TIME DE BASE
    	min = MIN; sec = SEC;

    	// Init timeline
    	time.setCycleCount(Timeline.INDEFINITE);
    	time.getKeyFrames().add(k);
    	time.playFromStart();
    }

	public void tempsEcoule() {
		time.stop();

		Alert a = new Alert(AlertType.ERROR);
		a.setContentText("Temps Ecoulé !");
		a.show();

		// Suppression partie et restart
		btn_bar.getButtons().remove(0, btn_bar.getButtons().size());
		initialize();
	}
    public void hideAll(boolean hide) {
    	for (Node n : vbox_app.getChildren()) {
    		n.setVisible(!hide);
    	}
    }

    @FXML
    public void clickProposer() throws TropNulException, IOException {

    	if (txt_console.getText().isEmpty()) {
    		close();
    		throw new TropNulException();
    	}
    	// Get last resultat
    	String[] list_op = txt_console.getText().split("\n");

    	String last_result = list_op[list_op.length - 1].split("=")[1].trim();
    	System.out.println(last_result);

    	// Si valide get Nb operations et tabScore
    	if (!(label_solution.getText().trim().compareTo(last_result) == 0)) {
    		close();
    		throw new TropNulException();
    	}

    	txt_console.setText("");
    	// Alert Box de victoire
    	Alert victory = new Alert(AlertType.INFORMATION);
    	victory.setTitle("Victoire !");
    	victory.setHeaderText("bravo tu é tré for");
    	victory.showAndWait();

    	// Suppression de toutes les plaques
    	btn_bar.getButtons().remove(0, btn_bar.getButtons().size());
    	// Ecriture dans le tab des scores
    	writeScore(pseudo,list_op.length,"420:51");
    	// Reset le jeu
    	initialize();
    }
    public void setRouge(Node n) {
    	setRouge(n);
    }
    public void close() {
    	Stage stage = (Stage) txt_operation.getScene().getWindow();
    	stage.close();
    }
    /**
     * Initialise les boutons dans le tab arg
     * @param aInit
     * @return
     */
    public Button[] initBtn(Button[] aInit) {
    	for (int i = 0 ; i < aInit.length; i++) {
    		aInit[i] = new Button();
    	}
    	return aInit;
    }

    /**
     * Tire et affecte les valeurs aléatoires aux boutons et à la solution
     */
    public Button[] prepNombres(Button[] nombres) {

    	label_solution.setText("" + alea());
    	for (int i = 0; i < nombres.length; i++) {
    		nombres[i].setText(" " + alea());
    	}

    	return nombres;
    }


   /**
    * Génère un nombre entier aléatoire entre 0 et BORNE_SUP compris
    * @return entier aléatoire
    */
    public int alea() {
    	Random rng = new Random();
    	return rng.nextInt(BORNE_SUP + 1);
    }

    public void d(String t) {
    	if (DEBUG) System.out.println(t);
    }

    /**
     * Affiche text dans la TextArea txt_console
     * @param text string a afficher
     */
    public void console(String text) {
    	txt_console.appendText(text + '\n');
    }


    public void writeScore(String pseudo, int score, String timer) throws IOException {
    	BufferedWriter w = new BufferedWriter(new FileWriter(SCORES,true));
    	w.write(pseudo + " - " + score + " coups, temps restant : " +
    	String.format("%02d:%02d",min,sec) + "\n");
    	w.close();
    }

    // Todo trier score
    public String readScore() throws FileNotFoundException {
    	String scores = "";
    	Scanner r = new Scanner(SCORES);
    	for (;r.hasNextLine();) {
    		scores += r.nextLine() + "\n";
    	}


    	return scores;
    }

    @FXML
    public void clickScores() throws FileNotFoundException {
    	Alert sc = new Alert(AlertType.INFORMATION);
    	sc.setTitle("Scores");
    	sc.setHeaderText("Scores :");
    	sc.setContentText(readScore());

    	sc.showAndWait();
    }
    /**
     * Active ou désactive l'intéraction utilisateur sur les plaques nombres
     * @param activer boolean : true -> activer, false -> désactiver
     */
    public void activerNombres(boolean activer) {
    	Button btn;
    	for (Node b : btn_bar.getButtons() ) {
    		btn = (Button) b;
    		btn.setDisable(!activer);
    	}

    }

    /**
     * Active ou désactive l'intéraction utilisateur sur les opérations
     * @param activer boolean : true -> activer, false -> désactiver
     */
    public void activerOperations(boolean activer) {
    	Button btn;
    	for (Node b : hbox_operations.getChildren() ) {
    		btn = (Button) b;
    		btn.setDisable(!activer);
    	}

    }

    /**
     * Efface le texte contenu dans le TextField txt_operation
     */
    public void clearTxtOperation() {
    	txt_operation.setText("");
    }

    /**
     * Supprime la saisie utilisateur après une saisie valide d'opération
     */
    public void clearSaisie() {
    	activerNombres(true);
		activerOperations(false);
		clearTxtOperation();
    }

    /**
     * Retourne le bouton contenant le texte id
     * @param id
     * @return
     */
    public Button rechNombre(String id) {
    	Button b = null;

    	for(Node n : btn_bar.getButtons()) {
    		b = (Button) n;

    		if (b.getText().compareTo(id) == 0) {
    			return b;
    		}
    	}

    	return null;
    }
}
