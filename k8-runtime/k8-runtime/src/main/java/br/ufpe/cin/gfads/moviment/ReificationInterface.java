package br.ufpe.cin.gfads.moviment;

import java.util.List;

/**
 * 
 * @author Adalberto Jr.
 *
 */
public interface ReificationInterface {

	boolean move(List<Moviment> adaptationScript);
	
	boolean move(Moviment moviment);
}