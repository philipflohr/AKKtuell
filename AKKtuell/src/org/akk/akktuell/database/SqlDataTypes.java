package org.akk.akktuell.database;

public enum SqlDataTypes {
	/**Integer*/
	INTEGER("INTEGER"),
	/**float*/
	FLOAT("DOUBLE"),
	/**Up to 65535 chars*/
	STRING("TEXT"),
	/**Up to 255 chars*/
	VARCHAR("VARCHAR"),
	/**Date, saved as ISO8601-string ("YYYY-MM-DD HH:MM:SS.SSS").*/
	DATE("VARCHAR(23)"), 
	/**Long text*/
	TEXT("TEXT"),
	/**Single digit*/
	SINGLE_INT("INTEGER(1)"); //TODO check, if working, TINYINT might be better
	
	private final String type;
	private SqlDataTypes(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return this.type;
	}	
}
