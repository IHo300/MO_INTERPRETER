package model.objects;
import model.Console;
import java.lang.NumberFormatException;

public class PseudoValue {

	private Object value;
	private PrimitiveType primitiveType = PrimitiveType.NOT_YET_IDENTIFIED;
	private boolean constFlag = false;

	public PseudoValue(Object value, PrimitiveType primitiveType) {
		this.primitiveType = primitiveType;
		this.value = null;
	}
	
	public PseudoValue(Object value, String primitiveType) {

		PrimitiveType type = resolve_type(primitiveType);

		if (value == null || checkValueType(value, type)) {
			this.value = value;
			this.primitiveType = type;
		}

	}

	private PseudoValue(Object value, PrimitiveType primitiveType, boolean constFlag) {
		this.value = value;
		this.primitiveType = primitiveType;
		constFlag = constFlag;
	}

	public PseudoValue(PseudoValue v) {
		this(v.value, v.primitiveType, v.constFlag);
	}
	
	public void setPrimitiveType(String primitiveType) {
		PrimitiveType type = resolve_type(primitiveType);
		this.primitiveType = type;
	}
	
	public void reset() {
		this.value = null;
	}

	public void markConst() {
		this.constFlag = true;
	}
	
	public boolean isConst() {
		return this.constFlag;
	}

	public PrimitiveType getPrimitiveType() {
		return this.primitiveType;
	}
	
    public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	
	public static boolean checkValueType(Object value, PrimitiveType primitiveType) {

		switch(primitiveType) {
			case BOOLEAN:
				return value instanceof Boolean;
			case INT:
				return value instanceof Integer;
			case FLOAT:
				return value instanceof Float;
			case STRING:
				return value instanceof String;
			case ARRAY:
				return value instanceof PseudoArray;
			default:
				return false;
		}
	}

	
	public static PrimitiveType resolve_type(String primitiveTypeString) {
		
		PrimitiveType primitiveType = PrimitiveType.NOT_YET_IDENTIFIED;
		
		if(primitiveTypeString.contains("bool")) {
			primitiveType = PrimitiveType.BOOLEAN;
		}
		else if(primitiveTypeString.contains("int")) {
			primitiveType = PrimitiveType.INT;
		}
		else if(primitiveTypeString.contains("float")) {
			primitiveType = PrimitiveType.FLOAT;
		}
		else if(primitiveTypeString.contains("String")) {
			primitiveType = PrimitiveType.STRING;
		} else if(primitiveTypeString.contains("array")) {
			primitiveType = PrimitiveType.ARRAY;
		}
	
		return primitiveType;
	}
}