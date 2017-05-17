package infof302.pieces;

import java.util.HashMap;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

/**
 * Classe qui va repésenter le domaine qui prendra une piece.
 * 
 * @author Abde-
 *
 */
public class PieceDomaine {
	
	public static HashMap<String,String> reverse = new HashMap<String,String>();
	private int x;
	private int y;
	private int i;
	private int j;
	private int coeff;
	private String op;

	static {
        reverse.put("=", "!=");
        reverse.put("!=", "=");
        reverse.put("<", ">=");
        reverse.put("<=", ">");
        reverse.put(">", "<=");
        reverse.put(">=", "<");
    }
	
	public PieceDomaine(int x, int y, int i ,int j ,int coeff, String op) {

		this.x = x;
		this.y = y;
		this.i = i;
		this.j = j;
		this.coeff = coeff;
		this.op = op;
	}
	
	/**
	 * Voir si piece se trouve dans le domaine de PieceGenerique.
	 * 
	 * @param generique
	 * 			la piece generique
	 * @param piece
	 * 			la piece à checker
	 * @param model
	 * 			modele dans lequel on applique la contrainte
	 * @return
	 */
	public Constraint apply(PieceGenerique generique, Piece piece, Model model){
		
		Constraint cons = 
				model.scalar(
						new IntVar[]{generique.getCoordX(),generique.getCoordY(),piece.getCoordX(),piece.getCoordY()},
						new int[]{x,y,i,j},
						reverse.get(op),
						-coeff);
		
		return cons;
	}
	
	public Constraint inDomain(PieceGenerique generique, int casex, int casey, Model model){
		
		/* 1*x + 1*y + 2*i + 2*j = 0, ici i et j les coordonn�es de la case (i,j).
		 * alors on doit passer 2i et 2j de l'autre cot� tq 1*x + 1*y = - 2*i - 2*j
		 */
		Constraint cons = 
				model.scalar(
						new IntVar[]{generique.getCoordX(),generique.getCoordY()},
						new int[]{x,y},
						op,
						-coeff - i*casex - j*casey);
		
		return cons;
	}
}
