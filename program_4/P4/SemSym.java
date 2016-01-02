import java.util.*;

public class SemSym {
    private String type;
	private String name;
    
    public SemSym(String type) {
        this.type = type;
    }

	// Store the name and corresponding type //
	public SemSym(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}
    
    public String getType() {
        return type;
    }
    
    public String toString() {
        return "("+type+")"+name;
    }
}

class FunctionSemSym extends SemSym {
	private String name;
	private String retType;
	private int numParams;
	private LinkedList paramTypes;

	// Expand it out for functions //
	public FunctionSemSym(String name, String type, int numParams) {
		super(name, type);
		this.name = name;
		this.retType = type;
		this.numParams = numParams;
	}

	public void addParamTypes(LinkedList L) {
		paramTypes = L;
	}

	public String getName() {
		return name;
	}
    
    public String getType() {
		String ret = "";
		for(int i = 0; i < numParams; i++) {
			ret += paramTypes.get(i);
			if(i != numParams - 1)
				ret += ",";
		}

		ret += "->" + retType;

        return ret;
    }

	public int getNumParams() {
		return numParams;
	}
    
    public String toString() {
		// Type of function //
		String ret = "(" + retType + ")" + name + "\n";

		// Param Types //
		for(int i = 0; i < numParams; i++)
			ret += i + ":" + paramTypes.get(i) + ",";

		// Return Type //
		ret += "->" + retType;
		
        return ret;
    }


}
