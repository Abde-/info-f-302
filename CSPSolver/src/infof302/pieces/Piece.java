package infof302.pieces;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Piece {
	
	Model model;
	IntVar coordx;
	IntVar coordy;
	
	/**
	 * Méthode qui met à jour le model `model` avec les contraintes de la piece en question
	 * tel que ça respecte la dépendence.
	 * 
	 * @param piece piece avec laquelle checker la dependence
	 */
	public void checkDependency(Piece piece){}
	
	/**
	 * Méthode qui met à jour le model `model` avec les contraintes de la piece en question
	 * tel que ça respecte l'indépendence.
	 * 
	 * @param piece piece avec laquelle checker la dependence
	 */	
	public void checkIndependency(Piece piece){}
	
	public IntVar getCoordX(){
		return coordx;
	}
	public IntVar getCoordY(){
		return coordy;
	}
	
	
}
