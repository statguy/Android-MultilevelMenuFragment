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

import java.util.List;

import org.w3c.dom.Node;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;

public abstract class MultilevelMenuActivity extends ActionBarActivity implements MultilevelMenuItemFactory {
	private static final String TAG = "MultilevelMenuActivity";
	MultilevelMenu menu;
	
	protected void replaceMenuFragment(Node menuItem) throws Exception
	{
		MultilevelMenuFragment menuFragment = new MultilevelMenuFragment();
		List<MultilevelMenuItem> menuItems = menu.parse(menuItem);
        ArrayAdapter<MultilevelMenuItem> adapter = getMultilevelMenuViewAdapter(menuItems);
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

        if (menu == null) {
			try {
				menu = new MultilevelMenu(this, this, getMultilevelMenuResourceId());
				Node menuRootItem = menu.getMenuRootElement();
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
	protected abstract ArrayAdapter<MultilevelMenuItem> getMultilevelMenuViewAdapter(List<MultilevelMenuItem> items);
	protected abstract int getMultilevelMenuFragmentContainerResourceId();
}
