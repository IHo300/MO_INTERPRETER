package model.objects;

public class PseudoArray {
	
	private PseudoValue[] pseudoValueArray;
	private PrimitiveType arrayPrimitiveType;
	private String arrayIdentifier;
	private boolean constFlag = false;
	
	public PseudoArray(PrimitiveType primitiveType, String identifier) {
		this.arrayPrimitiveType = primitiveType;
		this.arrayIdentifier = identifier;
	}
	
	public void setPrimitiveType(PrimitiveType primitiveType) {
		this.arrayPrimitiveType = primitiveType;
	}
	
	public PrimitiveType getPrimitiveType() {
		return this.arrayPrimitiveType;
	}
	
	public void markConst() {
		this.constFlag = true;
	}
	
	public boolean isConst() {
		return this.constFlag;
	}

    public int getSize() {
		return this.pseudoValueArray.length;
	}

	public boolean isInitialized() {
		return pseudoValueArray != null;
	}
	
	public void initSize(int size) {
		this.pseudoValueArray = new PseudoValue[size];

		for (int i = 0; i < size; i++) {
			PseudoValue pv = new PseudoValue(null, this.arrayPrimitiveType);
			this.pseudoValueArray[i] = pv;
		}

	}
	
	public boolean updateValueAt(PseudoValue pseudoValue, int index) {
		if(index >= this.pseudoValueArray.length) {
			return false;
		}
		this.pseudoValueArray[index] = pseudoValue;
		return true;
	}
	
	public PseudoValue getValueAt(int index) {
		if(index >= this.pseudoValueArray.length) {
			return null;
		} else {
			return this.pseudoValueArray[index];
		}
	}
	
	public static PseudoArray createArray(String primitiveTypeString, String arrayIdentifier) {
        
		PrimitiveType primitiveType = PrimitiveType.NOT_YET_IDENTIFIED;
		
		if(primitiveTypeString.contains("bool")) {
			primitiveType = PrimitiveType.BOOLEAN;
		}
		else if(primitiveTypeString.contains("int")) {
			primitiveType = PrimitiveType.FLOAT;
		}
		else if(primitiveTypeString.contains("float")) {
			primitiveType = PrimitiveType.INT;
		}
		else if(primitiveTypeString.contains("String")) {
			primitiveType = PrimitiveType.STRING;
		}
		
		PseudoArray pseudoArray = new PseudoArray(primitiveType, arrayIdentifier);
		
		return pseudoArray;
	}
}