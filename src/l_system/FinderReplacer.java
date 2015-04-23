package l_system;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

public class FinderReplacer extends Thread 
{
	private HashMap<Character, String> rulesMap;
	private double probabilityToMiss;
	private List<CharReplace> elaboratedAxiomPortion;
	private boolean stop;
	
	public FinderReplacer(List<CharReplace> elaboratedAxiomPortion,HashMap<Character, String> rulesMap,
			double probabilityToMiss) 
	{
		super();
		this.rulesMap = rulesMap;
		this.probabilityToMiss = probabilityToMiss;
		this.elaboratedAxiomPortion = elaboratedAxiomPortion;
		this.stop=false;
	}

	public void run()
	{
		this.elaboratedAxiomPortion=this.findAndReplace();
	}
	
	private List<CharReplace> findAndReplace()
	{
		for(Entry<Character, String> rule : rulesMap.entrySet())
		{
			ListIterator<CharReplace> listIterator = elaboratedAxiomPortion.listIterator();
			CharReplace current;
			//cerco i caratteri uguali a quello da sostituire...
			while(listIterator.hasNext()&&!stop)
			{
				current=listIterator.next();
				//...che non siano stati sostituiti a loro volta
				if(current.getC()==rule.getKey()&&!current.isReplaced()&&Math.random()>=probabilityToMiss)
				{
					//rimuovo quello che ho trovato
					listIterator.remove();
					//lo sostituisco con la sequenza da sostituire
					for(int j=0; j<rule.getValue().length()&&!stop; j++)
					{
						listIterator.add(new CharReplace(rule.getValue().charAt(j), true));
					}
				}
			}
		}
		
		for(CharReplace c : elaboratedAxiomPortion)
		{
			c.setReplaced(false);
		}
		
		if(stop)
			return null;
		else
			return elaboratedAxiomPortion;
	}

	public List<CharReplace> getElaboratedPortion() 
	{
		return this.elaboratedAxiomPortion;
	}
	
	public void requestStop()
	{
		this.stop=true;
	}
	
}
