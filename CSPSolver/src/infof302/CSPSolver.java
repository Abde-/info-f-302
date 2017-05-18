package infof302;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;

import infof302.pieces.*;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class CSPSolver {
	
	private static String problem;
	private static int dimension;
	private static int nbTour =0;
	private static int nbFou=0;
	private static int nbCavalier=0;
	private static int nbGenerique=0;
	private static int nbPieces;
	private static String genFile=null;
	private static ArrayList<ArrayList<Character>> board;

	/**
	 * Méthode pour rajouter élement e à l'array a.
	 * 
	 * @param a
	 * @param e
	 * @return
	 */
	public static <T> T[] addElement(T[] a, T e) {
	    a  = Arrays.copyOf(a, a.length + 1);
	    a[a.length - 1] = e;
	    return a;
	}
	
	//////////// METHODES POUR PRINT ////////////
	private static void printSol(Piece[] piece) {
		int coordX; int coordY;
		char pieceName;
		setBoard();
		for(int i=0; i<piece.length; ++i){
			coordX = piece[i].getCoordX().getValue() -1;
			coordY = piece[i].getCoordY().getValue() -1;
			pieceName = String.valueOf(piece[i].getCoordX()).charAt(0);
			board.get(coordX).set(coordY,pieceName);
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

	////////////////////////////////////////////
	//////////// METHODES POUR ARGS ////////////
	
	private static PieceDomaine[] parseDomains(String toParse){
		PieceDomaine[] domains = null;
		try {
			domains = new PieceDomaine[]{};
			
			File file = new File(toParse);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] data = line.split(" ");
				int x = Integer.parseInt(data[0]);
				int y = Integer.parseInt(data[1]);
				int i = Integer.parseInt(data[2]);
				int j = Integer.parseInt(data[3]);
				int coeff = Integer.parseInt(data[4]);
				String op = data[5];
				
				domains = addElement(domains, new PieceDomaine(x,y,i,j,coeff,op));
			}
			fileReader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return domains;
	}

	private static ArgumentParser argparse() {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("CSPSolver");
        parser.addArgument("-i")
        	  	.action(Arguments.storeTrue());
        parser.addArgument("-d")
        		.action(Arguments.storeTrue());
        parser.addArgument("-n")
        		.required(true)
        		.type(Integer.class);
        parser.addArgument("-t")
				.required(true)
				.type(Integer.class);
        parser.addArgument("-f")
				.required(true)
				.type(Integer.class);
        parser.addArgument("-c")
				.required(true)
				.type(Integer.class);
        parser.addArgument("-g")
				.type(Integer.class);
        parser.addArgument("-file")
			.type(String.class);
        
        return parser;
	}
	
	public static Boolean initParam(ArgumentParser parser, String[] args){
		Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
        	parser.handleError(e);
        	return false;
        }
        
        if(((boolean) ns.get("i") && (boolean) ns.get("d"))
        		|| ((!(boolean) ns.get("i") && !(boolean) ns.get("d")))){
        	return false;
        }

        problem = ((boolean) ns.get("i") ? "i" : "d");
        dimension = (int) ns.get("n");
        nbTour = (int) ns.get("t");
        nbFou = (int) ns.get("f");
        nbCavalier = (int) ns.get("c");
        nbGenerique = (ns.get("g") != null ? (int) ns.get("g") : 0);
        genFile = (ns.get("file") != null ? (String) ns.get("file") : "");
        
        return true;
	}
	
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Résoudre le problème de l'indépendence.
	 * 
	 * @param model
	 * @param pieces
	 */
	public static void checkIndependency(Model model, Piece... pieces){
		// contraintes
		for(int i = 0; i < nbPieces; ++i){
			pieces[i].checkIndependency(pieces);
		}
	}
	
	/**
	 * Résoudre le problème de la dépendence / domination.
	 * 
	 * @param model
	 * @param pieces
	 */
	public static void checkDependency(Model model, Piece... pieces){
		Constraint[] everyConstraint = new Constraint[]{};
		
		//d'abord checker qu'aucune pièce se trouve au même endroit
		for (int i = 0; i < pieces.length; ++i){
			for (int j = 0; j < pieces.length; ++j){
				pieces[i].checkEqual(pieces[j]);
			}
		}
		
		/* 
		 * pour chaque case de l'échiquier, checker les 4 contraintes->
		 * 1 .- chaque case [i,j] est occupée par une pièce OU
		 * 2 .- la case [i,j] est dominée par une tour OU
		 * 3 .- la case [i,j] est dominée par un fou OU
		 * 4 .- la case [i,j] est dominée par un cavalier OU
		*/
		
		for(int i=1; i<=dimension; ++i){
			for(int j=1; j<=dimension; ++j){
				Constraint[] perPieceConstraint = new Constraint[]{};
				
				for (Piece piece : pieces){

					Constraint constraint =
							model.or(
								piece.checkPieceExists(i,j),
								piece.inDomain(i, j)
							);

					perPieceConstraint = addElement(perPieceConstraint,constraint);
				}
				
				// cette contrainte represente que la case respecte toutes les contraintes
				Constraint dominatedPiece = model.or(perPieceConstraint);
				
				everyConstraint = addElement(everyConstraint, dominatedPiece);
			}
		}

		// checker que TOUTE case respecte toutes les contraintes
		model.and(everyConstraint).post();
		
	}
	
	public static void main (String[] args){
		//ARGPARSE
		ArgumentParser parser = argparse();
		 if(!initParam(parser, args)){
			System.out.println("Pas de solutions");
			return;
		}
		
		nbPieces = nbTour+nbCavalier+nbFou+nbGenerique;
		
		Model model = new Model();
		Piece[] piece =  new Piece[nbPieces];
		
		// créer pièces
		for(int i=0; i<nbTour; ++i){
			piece[i] = new Tour(model, dimension);
		}
		for(int i=nbTour; i<nbTour+nbCavalier; ++i){
			piece[i] = new Cavalier(model, dimension);
		}
		for(int i=nbTour+nbCavalier; i<nbTour+nbCavalier+nbFou; ++i){
			piece[i] = new Fou(model, dimension);
		}
		
		PieceDomaine[] domaines = null;
		if (nbGenerique != 0 && !genFile.equals("")){
			domaines = parseDomains(genFile);
		
			for(int i=nbTour+nbCavalier+nbFou; i<nbTour+nbCavalier+nbFou+nbGenerique; ++i){
				piece[i] = new PieceGenerique(model, domaines, dimension);
			}
		}
		
		if(problem.equals("i")){
			checkIndependency(model,piece);
		}
		else{
			checkDependency(model,piece);
		}
		
		Solution solution = model.getSolver().findSolution();
		if (solution != null){	
			printSol(piece);
		}
		else{

			System.out.println("Aucune solution trouvée");
		}
	
	}
	
}
