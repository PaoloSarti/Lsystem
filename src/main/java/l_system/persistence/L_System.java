package l_system.persistence;

import java.io.Serializable;
import java.util.List;

import l_system.gui.ProportionPoint;

public class L_System implements Serializable
{
	private static final long serialVersionUID = 2L;
	private String name;
	private String axiom;
	private List<String> rules;
	private int nIterations;
	private double angle;
	private ProportionPoint startingPoint;
	private double zoom;
	private double probabilityToMiss;
	private char invisibleChar;
	private long seed = 0L;

	public L_System(){

	}

	public L_System(String axiom, List<String> rules, int nIterations,
			double angle, ProportionPoint startingPoint, double zoom, String name, double probabilityToMiss, char invisibleChar, long seed)
	{
		this.axiom = axiom;
		this.rules = rules;
		this.nIterations = nIterations;
		this.angle = angle;
		this.startingPoint = startingPoint;
		this.zoom=zoom;
		this.name=name;
		this.probabilityToMiss=probabilityToMiss;
		this.invisibleChar=invisibleChar;
		this.seed = seed;
	}

	public L_System(String axiom, List<String> rules, int nIterations,
			double angle, ProportionPoint startingPoint, double zoom, String name, double probabilityToMiss)
	{
		this(axiom,rules,nIterations,angle,startingPoint,zoom,name,probabilityToMiss,' ', 0L);
	}

	public String getAxiom() 
	{
		return axiom;
	}


	public List<String> getRules() 
	{
		return rules;
	}


	public int getnIterations() 
	{
		return nIterations;
	}


	public double getAngle() 
	{
		return angle;
	}


	public ProportionPoint getStartingPoint() 
	{
		return startingPoint;
	}


	public double getZoom() 
	{
		return zoom;
	}


	public String getName() 
	{
		return name;
	}


	public double getProbabilityToMiss() 
	{
		return probabilityToMiss;
	}


	public void setProbabilityToMiss(double probabilityToMiss) 
	{
		this.probabilityToMiss = probabilityToMiss;
	}

	public boolean isValid()
	{
		if(this.getAxiom()!=null
				&&!this.getAxiom().equals("")
				&&this.getRules()!=null
				&&this.validRules(this.getRules())
				&&this.getnIterations()>=0
				&&this.getProbabilityToMiss()>=0
				&&this.getProbabilityToMiss()<=1
				&&this.getZoom()>0
				&&this.getStartingPoint()!=null)
			return true;
		else
			return false;
	}
	
	private boolean validRules(List<String> rules)
	{
		boolean valid = true;
		for(int i=0; i<rules.size()&&valid; i++)
		{
			if(rules.get(i).split("=").length!=2)
			{
				valid=false;
			}
		}
		
		return valid;
	}

	public char getInvisibleChar() 
	{
		return invisibleChar;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}
}
