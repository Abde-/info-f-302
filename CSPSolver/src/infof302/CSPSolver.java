package infof302;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;

import infof302.pieces.Cavalier;
import infof302.pieces.Piece;
import infof302.pieces.Tour;

public class CSPSolver {
	public static void main (String[] args){
		// main pour tester
		
		int length = 3;
		
		//pour test
		int nbpieces = 3;
		
		Model model = new Model("Problème d'indépendence");
		Piece[] piece =  new Piece[nbpieces];
		
		// definition de variables 
		for(int i = 0; i < nbpieces; ++i){
			if(Math.random() < (double) 0.5)
				piece[i] = new Cavalier(model,length);
			else
				piece[i] = new Tour(model,length);
		}
		
		// contraintes
		for(int i = 0; i < nbpieces; ++i){
			for(int j = 0; j < nbpieces; ++j){
				piece[i].checkDependency(piece[j]);
			}
		}
		
		Solution solution = model.getSolver().findSolution();
		if (solution != null){
			System.out.println(solution.toString());
		}
		
	
	}
}
