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

package jvj.android.multilevelmenufragment;

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

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;

public abstract class MultilevelMenuActivity extends ActionBarActivity {
	private static final String TAG = "MultilevelMenuActivity";
	
	protected Node getMenuRootElement(int resourceId) throws IOException, ParserConfigurationException, SAXException
	{
		Resources res = getResources();
		InputStream is = res.openRawResource(resourceId);
			
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(is);
		document.getDocumentElement().normalize();    
		is.close();
		
		return document.getDocumentElement();
	}

	protected List<MultilevelMenuItemInterface> parse(Node node) throws Exception
	{
        if (node.getNodeName().equals("menu"))
        {
        	List<MultilevelMenuItemInterface> items = new ArrayList<MultilevelMenuItemInterface>();
        	
        	NodeList subNodeList = node.getChildNodes();        	
        	for (int i=0; i<subNodeList.getLength(); i++)
        	{
        		Node subnode = subNodeList.item(i);
        		if (subnode.getNodeType() == Node.ELEMENT_NODE)
        		{
        			if (!subnode.getNodeName().equals("item"))
        				throw new Exception("Not an item node under menu.");
        			
        			MultilevelMenuItemInterface menuItem = getNewMultilevelMenuItem();
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
	
	protected void replaceMenuFragment(Node menuItem) throws Exception
	{
		MultilevelMenuFragment menuFragment = new MultilevelMenuFragment();
        List<MultilevelMenuItemInterface> menuItems = parse(menuItem);
        ArrayAdapter<MultilevelMenuItemInterface> adapter = getMultilevelMenuViewAdapter(menuItems);
        menuFragment.setListAdapter(adapter);
        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        
        if (menuItem.getParentNode() == menuItem.getOwnerDocument())
        {
        	ft.add(getMultilevelMenuFragmentContainerResourceId(), menuFragment);
        }
        else
        {
        	ft.replace(getMultilevelMenuFragmentContainerResourceId(), menuFragment);
        	ft.addToBackStack(null); // Enable back button to go back to the previous menu
        }
        ft.commit();
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
			try {
				Node menuRootItem = getMenuRootElement(getMultilevelMenuResourceId());
				replaceMenuFragment(menuRootItem);
			}
			catch (Exception e) {
				Log.e(TAG, "onCreate", e);
			}
        }
	}

	public void onMultilevelMenuItemSelected(Node menuItem) {
		try {
			replaceMenuFragment(menuItem);
		}
		catch (Exception e) {
			Log.e(TAG, "onMenuItemSelected", e);
		}
	}

	protected abstract int getMultilevelMenuResourceId();
	protected abstract MultilevelMenuItemInterface getNewMultilevelMenuItem();
	protected abstract ArrayAdapter<MultilevelMenuItemInterface> getMultilevelMenuViewAdapter(List<MultilevelMenuItemInterface> items);
	protected abstract int getMultilevelMenuFragmentContainerResourceId();
}
