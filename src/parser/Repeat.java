package parser;

import java.util.*;


public class Repeat {
   // private final int repeatCount;
    private List<Measure> measures;
    private Measure firstEnding;
    private Measure secondEnding;

    public Repeat(List<Measure> m) {
        //throw new RuntimeException("Not implemented.");
        measures= new ArrayList<Measure>(m);
        firstEnding= null;
        secondEnding= null;
    }
    public Repeat(List<Measure> m, Measure fe, Measure se){
    	measures= new ArrayList<Measure>(m);
    	if(fe!=null && se!=null){
        	firstEnding= new Measure(fe.getElements(), fe.getVoice());
        	secondEnding= new Measure(se.getElements(), se.getVoice());
    	}
    	else{
    		firstEnding= null;
            secondEnding= null;
    	}
    	
    }
    
    public List<Measure> getRepeatMeasures(){
    	List<Measure> meas= new ArrayList<Measure>();
    	meas.addAll(measures);
    	if (firstEnding != null && secondEnding != null){
    		meas.add(firstEnding);
    		meas.addAll(measures);
    		meas.add(secondEnding);
    	}
    	else{
    		meas.addAll(measures);
    	}
    	return meas;
    }
    
    public List<Measure> getMeasures(){
    	return measures;
    }
}
