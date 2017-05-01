package infof302.pieces;

import org.chocosolver.solver.Model;

public class Fou extends Piece{
	
	/**
	 * @param model
	 */
	public Fou(Model model, int n) {
		super(model);
		
		coordx = model.intVar("Fou(x)", 1, n);
		coordy = model.intVar("Fou(y)", 1, n);
	}
	
	@Override
	public void checkDependency(Piece piece){
		super.checkEqual(piece);
		
		// contrainte 3: pour toute piece i fou implique que les autres pieces j
		// tq j != i -> 
		
		// TODO: pas fini
		
		model.or(
			model.and(
				
				model.arithm(coordx, "=", coordy, "-", piece.coordy),
				model.arithm(coordx, "!=", coordy,"+",5)
			),
			
			model.and(
				model.arithm(coordx, "!=", coordy),
				model.arithm(coordx, "!=", coordy,"+",5)
			)
		).post();
	
	}
	
	@Override
	public void checkIndependency(Piece piece){
		
	}
}
