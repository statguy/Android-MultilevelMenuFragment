/*

(c) 2013 Jussi Jousimo, jvj@iki.fi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.codeandpop.android.multilevelmenufragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.res.Resources;

public class MultilevelMenu {
	private Context context;
	private MultilevelMenuItemFactory menuItemFactory;
	private int resourceId;
	
	public MultilevelMenu(Context context, MultilevelMenuItemFactory menuItemFactory, int resourceId) {
		this.context = context;
		this.menuItemFactory = menuItemFactory;
		this.resourceId = resourceId;
	}
	
	public Node getMenuRootElement() throws IOException, ParserConfigurationException, SAXException {
		Resources res = context.getResources();
		InputStream is = res.openRawResource(resourceId);
			
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(is);
		document.getDocumentElement().normalize();    
		is.close();
		
		return document.getDocumentElement();
	}

	public List<MultilevelMenuItem> parse(Node node) throws Exception {
        if (node.getNodeName().equals("menu")) {
        	List<MultilevelMenuItem> items = new ArrayList<MultilevelMenuItem>();
        	
        	NodeList subNodeList = node.getChildNodes();        	
        	for (int i=0; i<subNodeList.getLength(); i++) {
        		Node subnode = subNodeList.item(i);
        		if (subnode.getNodeType() == Node.ELEMENT_NODE) {
        			if (!subnode.getNodeName().equals("item"))
        				throw new Exception("Not an item node under menu.");
        			
        			MultilevelMenuItem menuItem = menuItemFactory.newMenuItem();
        			menuItem.parse(subnode);
        			items.add(menuItem);
        		}
        	}
        	
        	if (items.isEmpty())
        		throw new Exception("No items under menu.");
        	
        	return items;
        }
        else throw new Exception("Not a menu node.");
	}
}
