package org.akk.akktuell.database;

public enum DBFields {
	EVENT_NAME("name"),
	EVENT_DESC("description"),
	EVENT_TYPE("type"),
	EVENT_DATE("date");
	
	private final String name;
	
	DBFields(String name) {
		if (name == null)
			throw new IllegalArgumentException("Name must not be null.");
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	
}
