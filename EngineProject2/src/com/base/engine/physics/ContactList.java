package com.base.engine.physics;

public class ContactList {
	
	public static Contact[] contacts;
	public static int next = 0;
	
	public static void generate(int numContacts) {
		contacts = new Contact[numContacts];
		for(int i = 0; i < numContacts; i++) contacts[i] = new Contact();
	}
	
	public static void reset() {next = 0;}
	public static Contact getNext() {return contacts[next++];}
	//public static void iterate() {next++;}
	public static boolean hasNext() {return next < contacts.length;}
}
