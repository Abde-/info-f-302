package infof302.pieces;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

/**
 * Pièce qui représente une pièce générique.
 * 
 * TODO: Cette classe s'occupe de representer une piece générique (cfr question bonus).
 * 
 * @author Abde-
 *
 */
public abstract class Piece {
	
	Model model;
	IntVar coordx;
	IntVar coordy;
	
	/**
	 * @param model Modèle du problème à résoudre
	 */
	public Piece(Model model){
		this.model = model;
	}
	
	/**
	 * Méthode qui met à jour le model `model` avec les contraintes de la piece en question
	 * tel que ça respecte l'indépendence.
	 * 
	 * @param piece piece avec laquelle checker l'indépendence
	 */
	public abstract void checkIndependency(Piece[] pieces);
	
	/**
	 * Méthode qui renvoit une Constraint qui vérifie si la case (caseX, caseY) se trouve
	 * dans le domaine de la pièce.
	 * 
	 * @param caseX
	 * @param caseY 
	 */	
	public abstract Constraint inDomain(int caseX, int caseY);

	/**
	 * Méthode qui renvoit une Constraint qui vérifie si la pièce se trouve dans la case (caseX, caseY).
	 * 
	 * @param caseX
	 * @param caseY
	 */
	public Constraint checkPieceExists(int caseX, int caseY){
		
		// renvoit si coordx=caseX and coordy=caseY
		return model.and(
				model.arithm(coordx, "=", caseX),
				model.arithm(coordy, "=", caseY)
				);
	}
	
	/**
	 * Rajoute contrainte dans le 
	 * 
	 * @param piece
	 */
	public void checkEqual(Piece piece){	
		// contrainte 1: piece x différente piece y tq x != y
		
		if(this != piece)
			model.or(
				model.arithm(coordx, "!=", piece.coordx),
				model.arithm(coordy, "!=", piece.coordy)
				)
			.post();
		
	}
		
	public IntVar getCoordX(){
		return coordx;
	}
	public IntVar getCoordY(){
		return coordy;
	}
}