package infof302;

import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;

import infof302.pieces.*;

public class CSPSolver {
	
	private static String problem;
	private static int dimension;
	private static int nbTour =0;
	private static int nbFou=0;
	private static int nbCavalier=0;
	private static int nbPieces;
	private static ArrayList<ArrayList<Character>> board;
	
	private static void printSolution(String solution, int nbpieces){	
		setBoard();
		String[] sol = solution.split(", ");	
		
		//première pièce est dans "Solution: pièce(x)=a" donc faire un split avec "Solution:" 
		String firstPieceX = sol[0].split("Solution: ")[1];
		String firstPieceY = sol[1];
		board.get((Integer.parseInt(firstPieceY.split("=")[1]))-1)
			.set((Integer.parseInt(firstPieceX.split("=")[1])-1),firstPieceX.charAt(0));

		for (int i=2; i<nbpieces+2; i+=2){
			board.get(Integer.parseInt(sol[i+1].split("=")[1])-1)
				.set(Integer.parseInt(sol[i].split("=")[1])-1, (sol[i]).charAt(0));
		}
		
		printBoard();
	}

	private static void setBoard() {
		board = new ArrayList<ArrayList<Character>>(dimension);
		for (int k = 0; k < dimension; k++) {
		    board.add(new ArrayList<Character>(dimension));
		    for (int j = 0; j < dimension; j++){
		        board.get(k).add('*');
		    }
		}
	}
	
	private static void printBoard() {
		for(int i=0; i<board.size();++i){
			for(int j=0; j<board.size();++j){
				System.out.print(board.get(i).get(j) + " ");
			}
			System.out.println("");
		}
	}
	
	
	private static Boolean checkInt(String number){
		try{
			Integer.parseInt(number);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
	private static Boolean checkArgs(String[] args, int arg1, int arg2, String param){
		if(args[arg1].equals(param)){
			if (checkInt(args[arg2])){
				return true;
			}
		}
		return false;
	}

	private static Boolean setArgs(String[] args) {
		if(args.length == 9){
			//check le type du probleme
			if(args[0].equals("-i")){
				problem = "i";
			}else if(args[0].equals("-d")){
				problem = "d";
			}else{
				return false;
			}
			
			//check dimension de l'équiquier / nombre de tour, de fou et de cavalier
			if(checkArgs(args, 1, 2, "-n") &&
				checkArgs(args, 3, 4, "-t") &&
				checkArgs(args, 5, 6, "-f") &&
				checkArgs(args, 7, 8, "-c")){
				dimension = Integer.parseInt(args[2]);
				nbTour = Integer.parseInt(args[4]);
				nbFou = Integer.parseInt(args[6]);
				nbCavalier = Integer.parseInt(args[8]);
			}
			else{
				return false;
			}
		}
		return true;
	}
	
	public static void main (String[] args){
		if(!setArgs(args)){
			System.out.println("Pas de solutions");
			return;
		}

		nbPieces = nbTour+nbCavalier+nbFou;
		
		Model model = new Model();
		Piece[] piece =  new Piece[nbPieces];
		
		//créer pièces
		for(int i=0; i<nbTour; ++i){
			piece[i] = new Tour(model, dimension);
		}
		for(int i=nbTour; i<nbTour+nbCavalier; ++i){
			piece[i] = new Cavalier(model, dimension);
		}
		for(int i=nbTour+nbCavalier; i<nbTour+nbCavalier+nbFou; ++i){
			piece[i] = new Fou(model, dimension);
		}
		
		if(problem.equals("i")){
			// contraintes
			for(int i = 0; i < nbPieces; ++i){
				piece[i].checkDependency(piece);
			}
		}
		else{
			//ArrayList<> array
			for(int i=0; i<dimension; ++i){
				for(int j=0; j<dimension; ++j){
					//????? je sais pas vmt quoi faire la ...
					//Constraint u1 = 
					//Constraint u3 = 
					//Constraint u4 = 
					//Constraint u5 = 
					//model.or(u1, u2, u3, u4); pour la case i,j 
					//array.push()
					piece[i].checkIndependency(piece, i, j);
				}
			}
		}
		
		Solution solution = model.getSolver().findSolution();
		if (solution != null){
			printSolution(solution.toString(), nbPieces);
		}
		else{
			System.out.println("Aucune solution trouvée");
		}
	
	}
}
