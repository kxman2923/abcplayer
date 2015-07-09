package player;

/**
 * Represents a pair of objects.
 This class is used in Player to map both a note and an octave conbination to an accidental.
*/
public class Pair<A, B> {
    public final A first;
    public final B second;
    
    public Pair(A first, B second) {
    	this.first = first;
    	this.second = second;
    }
    
    @Override
    public int hashCode() {
    	int hashFirst = first != null ? first.hashCode() : 0;
    	int hashSecond = second != null ? second.hashCode() : 0;
        
    	return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }
    
    @Override
    public boolean equals(Object other) {
    	if (other instanceof Pair) {
            Pair otherPair = (Pair) other;
    		return 
    		((  this.first == otherPair.first ||
    			( this.first != null && otherPair.first != null &&
    			  this.first.equals(otherPair.first))) &&
    		 (	this.second == otherPair.second ||
    			( this.second != null && otherPair.second != null &&
    			  this.second.equals(otherPair.second))) );
    	}

    	return false;
    }

    @Override
    public String toString() { 
           return "Pair<" + first + ", " + second + ">"; 
    }
}
