package l_system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

//import timeOffset.TimeOffset;


public class StringProcessingThread extends Thread
{
	private static final int THRESHOLD_FOR_MULTITHREAD = 160;
	private String axiom;
	private HashMap<Character, String> rulesMap;
	private int n;
	private double probabilityToMiss;
	private String command;
	private Controller controller;
	private int nAvaiableProcessors;
	private StringBuilderThread[] stringBuilders;
	//private TimeOffset timeOffset;
	private FinderReplacer[] findersReplacers;
	
	public StringProcessingThread(String axiom, List<String> rules, int n, double probabilityToMiss, String command, Controller controller)
	{
		this.axiom=axiom;
		this.n=n;
		this.probabilityToMiss=probabilityToMiss;
		this.command=null;
		this.controller=controller;
		this.nAvaiableProcessors=Runtime.getRuntime().availableProcessors();
		this.stringBuilders = new StringBuilderThread[nAvaiableProcessors];
		this.rulesMap= new HashMap<Character, String>();
		for(String rule : rules)
		{
			String[] ruleSplitted=rule.split("=");
			char toReplace = ruleSplitted[0].charAt(0); //si suppone che sia di un solo carattere
			String replaceWith = ruleSplitted[1];
			rulesMap.put(toReplace, replaceWith);
		}
	}
	
	
	public void run()
	{
		//timeOffset=new TimeOffset();
		//struttura di supporto
		List<CharReplace> elaboratedAxiom = this.charsFromString();
		
		//elaborazione
		try 
		{
			elaboratedAxiom=this.l_SystemString(elaboratedAxiom,n);
			//ricostruisco una stringa
			//timeOffset.restart();
			command=this.stringFromChars(elaboratedAxiom);
		} 
		catch (InterruptedException e) 
		{
			for(StringBuilderThread stringBuilder : stringBuilders)
			{
				if(stringBuilder!=null&&stringBuilder.isAlive())
				{
					stringBuilder.requestStop();
					//System.out.println("Chiudo uno StringBuilder");
				}
			}
			for(FinderReplacer finderReplacer : findersReplacers)
			{
				if(finderReplacer!=null&&finderReplacer.isAlive())
				{
					finderReplacer.requestStop();;
					//System.out.println("Chiudo un finderReplacer");
				}
			}
			e.printStackTrace();
		}
			

		//System.out.println(timeOffset.getOffsetMillis());
		if(command!=null)
			controller.drawCommand(command);
	}
	
	private List<CharReplace> charsFromString()
	{
		//struttura di supporto
		List<CharReplace> elaboratedAxiom = new ArrayList<CharReplace>();
					
		for(int i=0; i<axiom.length(); i++)
		{
			elaboratedAxiom.add(new CharReplace(axiom.charAt(i), false));
		}
		return elaboratedAxiom;
	}
	
	/*
	private String stringFromCharsOld(List<CharReplace> elaboratedAxiom)
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<elaboratedAxiom.size(); i++)
		{
			sb.append(elaboratedAxiom.get(i).getC());
		}
		return sb.toString();
	}
	*/
	
	private String stringFromChars(List<CharReplace> elaboratedAxiom) throws InterruptedException
	{
		StringBuilder sb = new StringBuilder();
		if(elaboratedAxiom.size()>=THRESHOLD_FOR_MULTITHREAD)
		{
			int sizeOfPortion = elaboratedAxiom.size()/(nAvaiableProcessors+1);
			stringBuilders = new StringBuilderThread[nAvaiableProcessors];
			for(int i=0; i<nAvaiableProcessors; i++)
			{
				if(i<nAvaiableProcessors-1)
				{
					stringBuilders[i] = new StringBuilderThread(new LinkedList<CharReplace>(elaboratedAxiom.subList(i*sizeOfPortion, (i+1)*sizeOfPortion)));
				}
				else
					stringBuilders[nAvaiableProcessors-1] = new StringBuilderThread(new LinkedList<CharReplace>(elaboratedAxiom.subList(i*sizeOfPortion, elaboratedAxiom.size())));
				stringBuilders[i].start();
			}
			
			for(int i=0; i<nAvaiableProcessors; i++)
			{
				stringBuilders[i].join();
			}
			
			sb = new StringBuilder();
			for(int i=0; i<nAvaiableProcessors; i++)
			{
				sb.append(stringBuilders[i].getRebuiltString());
			}
		}
		else
		{
			for(int i=0; i<elaboratedAxiom.size(); i++)
			{
				sb.append(elaboratedAxiom.get(i).getC());
			}
		}
		return sb.toString();
	}
	
	private List<CharReplace> l_SystemString(List<CharReplace> elaboratedAxiom, int n) throws InterruptedException
	{
		if(n>0)
		{
			if(elaboratedAxiom.size()>THRESHOLD_FOR_MULTITHREAD)
			{
				int sizeOfPortion = elaboratedAxiom.size()/(nAvaiableProcessors+1);
				findersReplacers = new FinderReplacer[nAvaiableProcessors];
				for(int i=0; i<nAvaiableProcessors; i++)
				{
					if(i<nAvaiableProcessors-1)
					{
						findersReplacers[i] = new FinderReplacer(new LinkedList<CharReplace>(elaboratedAxiom.subList(i*sizeOfPortion, (i+1)*sizeOfPortion)),
								rulesMap, probabilityToMiss);
					}
					else
						findersReplacers[nAvaiableProcessors-1] = new FinderReplacer(new LinkedList<CharReplace>(elaboratedAxiom.subList(i*sizeOfPortion, elaboratedAxiom.size())),
								rulesMap, probabilityToMiss);
					findersReplacers[i].start();
				}
				
				for(int i=0; i<nAvaiableProcessors; i++)
				{
					findersReplacers[i].join();
				}
				
				elaboratedAxiom = findersReplacers[0].getElaboratedPortion();
				for(int i=1; i<nAvaiableProcessors; i++)
				{
					elaboratedAxiom.addAll(findersReplacers[i].getElaboratedPortion());
				}
			}
			else
			{
				elaboratedAxiom=this.findAndReplace(elaboratedAxiom);
			}
			
			return l_SystemString(elaboratedAxiom, n-1);
		}
		else if(n==0)
		{
			return elaboratedAxiom;
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
	
	private List<CharReplace>  findAndReplace(List<CharReplace> elaboratedAxiom)
	{
		
		for(Entry<Character, String> rule : rulesMap.entrySet())
		{
			//get an iterator
			ListIterator<CharReplace> listIterator = elaboratedAxiom.listIterator();
			CharReplace current;
			//cerco i caratteri uguali a quello da sostituire...
			while(listIterator.hasNext())
			{
				current=listIterator.next();
				//...che non siano stati sostituiti a loro volta
				if(current.getC()==rule.getKey()&&!current.isReplaced()&&Math.random()>=probabilityToMiss)
				{
					//rimuovo quello che ho trovato
					listIterator.remove();
					//lo sostituisco con la sequenza da sostituire
					for(int j=0; j<rule.getValue().length(); j++)
					{
						listIterator.add( new CharReplace(rule.getValue().charAt(j), true));
					}
				}
			}
		}
		
		for(CharReplace c : elaboratedAxiom)
		{
			c.setReplaced(false);
		}
		
		return elaboratedAxiom;
	}
	
	
	public String getCommand()
	{
		return this.command;
	}
	
}
