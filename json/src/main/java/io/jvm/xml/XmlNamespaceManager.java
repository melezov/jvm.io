package io.jvm.xml;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An ad-hoc implementation of an XmlNamespaceManager.
 * Provides operations for adding/removing and resolving namespaces,
 * and scope management.
 * 
 * Partially mirrors the functionality of .NET's System.Xml.XmlNamespaceManager.
 * 
 * Used for the implementation of XmlJsonSerializer and XmlJsonDeserializer.
 */
public class XmlNamespaceManager {	
	
	/* Fields */
	
	/**
	 * A stack of namespace prefix:uri mappings
	 */
	private Deque<Map<String,String>> xmlNamespaceScopes;
	
	/* Properties */
		
	private String defaultNamespace="";
	
	/**
	 * The default namespace URI if exists, otherwise an empty string
	 */
	public String getDefaultNamespace() {
		return defaultNamespace;
	}

	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}		
	
	/* Methods */	

	public XmlNamespaceManager(){
		xmlNamespaceScopes = new ArrayDeque<Map<String,String>>();	
	}
	
	public void pushScope(){
		xmlNamespaceScopes.push(new HashMap<String, String>());
	}	
	
	public Map<String,String> popScope(){
		return xmlNamespaceScopes.pop();
	}	
	
	/**
	 * Add a namespace to the current namespace context
	 */
	public void addNamespace(String prefix, String uri){		
		getCurrentScope().put(prefix, uri);
	}
	
	/**
	 * Remove a namespace from the current namespace context
	 */
	public void removeNamespace(String prefix, String uri){		
		getCurrentScope().remove(prefix);		
	}
	
	/**
	 * Gets the namespace URI in the current scope for the given prefix.
	 * If the prefix is an empty string, returns the default namespace
	 */
	public String lookupNamespace(String prefix){
		if(prefix=="")
			return getDefaultNamespace();
		else
			return getCurrentScope().get(prefix);		
	}
	
	/**
	 * Finds the first prefix for a given URI in the current scope and returns
	 * it if it exists, otherwise returns "". If the URI is null, returns null.
	 */
	public String lookupPrefix(String uri) {
		if (uri == null)
			return null;
		else {
			Map<String, String> namespaceScope = xmlNamespaceScopes.peek();

			for (Entry<String, String> entry : namespaceScope.entrySet()) {
				if (uri.equals(entry.getValue()))
					return entry.getKey();				
			}
			return "";
		}
	}
	
	/**

	 * The given prefix has a namespace defined for current scope
	 */
	public boolean hasNamespace(String prefix){
		return getCurrentScope().get(prefix) != null;
	}
	
	/* Private methods */
	
	private Map<String,String> getCurrentScope(){
		return xmlNamespaceScopes.peek();
	}
	
	/* TODOs */	
	// Not reimplemented from .NET's System.Xml.*:
	// Data types:
	//     - NameTable
	//     - XmlNamespaceScope 
	// Constructors:
	//     - XmlNamespaceManager(NameTable)
	// Methods:
	//     - getNamespacesInScope
	//     - getEnumerator

}
