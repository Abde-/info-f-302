package infof302;

import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class HorseMinimizer {
	
	private static int n;
	
	/**
	 * Méthode pour rajouter élement e à l'array a.
	 * 
	 * @param a
	 * @param e
	 * @return
	 */
	public static Constraint[] addElement(Constraint[] a, Constraint e) {
	    a  = Arrays.copyOf(a, a.length + 1);
	    a[a.length - 1] = e;
	    return a;
	}
	
	/**
	 * Afficher solution
	 * @param cases
	 * @param min
	 */
	public static void printSolution(char[][] cases, int min){
		System.out.println("Nombre minimum: "+Integer.toString(min));

		for (int i = 0; i < n; ++i){
			for (int j = 0; j < n; ++j){
				System.out.print(cases[i][j]);
			}
			System.out.println();
		}
	};

	public static void main(String[] args){
		
		// taille pour test
		n = 5;
		
		Model model = new Model();
		IntVar[][] cases = new IntVar[n][n];
		IntVar minimize = model.intVar("Nombre de cavaliers",1, n*n);
		
		// initialisation de variables par case
		for (int i = 0; i < n; ++i){
			for (int j = 0; j < n; ++j){
				cases[i][j] = 
						model.intVar(
							"Case ("+Integer.toString(i)+
							"),("+Integer.toString(j)+")",
						0, 1);
			}
		}
		
		int[] k = {-2,2};
		int[] l = {-1,1};
		
		//pour chaque case, checker la contrainte
		for (int i = 0; i < n; ++i){
			for (int j = 0; j < n; ++j){
				
				Constraint[] constraints = new Constraint[]{};
				
				// si la case est dominée
				for (int offsetK : k){
					for (int offsetL : l){
						Constraint x = null;
						if (i+offsetK >= 0 && j+offsetL >= 0 && 
							i+offsetK < n && j+offsetL < n){
							
							x = model.arithm(cases[i+offsetK][j+offsetL], "=", 1);
							constraints = addElement(constraints,x);
						}
						if (i+offsetL >= 0 && j+offsetK >= 0 && 
								i+offsetL < n && j+offsetK < n){			
							x = model.arithm(cases[i+offsetL][j+offsetK], "=", 1);
							constraints = addElement(constraints,x);
						}
						
					}
				}
				
				// si la case est occupée
				Constraint x = model.arithm(cases[i][j], "=", 1);
				constraints = addElement(constraints,x);
				
				// case occupée ou dominée
				model.or(constraints).post();
			}
		}
		
		// convertir en array à 1D car la fonction sum prend que des array
		// à une dimension
		IntVar[] cases_to_minimize = new IntVar[n*n];
		for (int i = 0; i < n; ++i){
			for (int j = 0; j < n; ++j){
				cases_to_minimize[i*n+j] = cases[i][j];
			}
		}
		
		// la somme de chevaliers
		model.sum(cases_to_minimize, "=", minimize).post();
		
		// on minimuse le nombre maximum de chevaliers
		model.setObjective(Model.MINIMIZE, minimize);
		
		Solver solver = model.getSolver();
		
		int leastHorsemen = 0;
		char[][] printableCases = new char[n][n];
		
		// itérer jusqu'à trouver la meilleure solution
		while(solver.solve()){
		    leastHorsemen = minimize.getValue();
			for (int i = 0; i < n; ++i){
				for (int j = 0; j < n; ++j){
					printableCases[i][j] = (cases[i][j].getValue() == 1 ? 'C' : '*');
				}
			}
		}
		// imprimer solution
		printSolution(printableCases, leastHorsemen);
	}
	
}
