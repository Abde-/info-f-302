package infof302;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;

import infof302.pieces.*;

public class CSPSolver {
	public static void main (String[] args){
		// main pour tester
		
		int length = 6;
		
		//pour test
		int nbpieces = 7;
		
		Model model = new Model("Problème d'indépendence");
		Piece[] piece =  new Piece[nbpieces];
		
		// definition de variables 
		for(int i = 0; i < nbpieces; ++i){
			if(Math.random() < (double) 0.33)
				piece[i] = new Cavalier(model,length);
			else if (Math.random() < (double) 0.66)
				piece[i] = new Tour(model,length);
			else
				piece[i] = new Fou(model,length);
		}
		
		// contraintes
		for(int i = 0; i < nbpieces; ++i){
			piece[i].checkDependency(piece);
		}
		
		Solution solution = model.getSolver().findSolution();
		if (solution != null){
			System.out.println(solution.toString());
		}
		
	
	}
}
