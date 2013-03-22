/*
 *  soapUI, copyright (C) 2004-2011 smartbear.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.eclipse.prefs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.actions.EditorPrefs;
import com.eviware.soapui.actions.SoapUIPreferencesAction;
import com.eviware.soapui.eclipse.SoapUIActivator;
import com.eviware.soapui.eclipse.forms.FormBuilder;
import com.eviware.soapui.model.propertyexpansion.PropertyExpansionUtils;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.settings.UISettings;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.types.StringToStringMap;
/**
 * 
 * @author Dain Nilsson
 */
public class EditorPreferencePage extends AbstractPreferencePage<EditorPrefs> {
	public static final String EDITOR_FONT = "Editor Font";
	
	public EditorPreferencePage() {
		super(new EditorPrefs(SoapUIPreferencesAction.EDITOR_SETTINGS));
	}

	protected void buildForm(FormBuilder editorForm) {
		
		editorForm.appendFont(EDITOR_FONT);
		
		editorForm.appendCheckBox(EditorPrefs.XML_LINE_NUMBERS, "(show line-numbers in xml editors by default)", true);
		editorForm.appendCheckBox(EditorPrefs.GROOVY_LINE_NUMBERS, "(show line-numbers in groovy editors by default)", true);

		editorForm.appendCheckBox(EditorPrefs.NO_RESIZE_REQUEST_EDITOR, "(disables automatic resizing of request editors)", true);
		editorForm.appendCheckBox(EditorPrefs.START_WITH_REQUEST_TABS, "(defaults the request editor to the tabbed layout)", true);
		editorForm.appendCheckBox(EditorPrefs.AUTO_VALIDATE_REQUEST, "(always validate request messages before they are sent)", true);
		editorForm.appendCheckBox(EditorPrefs.ABORT_ON_INVALID_REQUEST, "(aborts invalid requests)", true);
		editorForm.appendCheckBox(EditorPrefs.AUTO_VALIDATE_RESPONSE, "(always validate response messages)", true);
		
	}
	
	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();

		Settings settings = SoapUI.getSettings();

		StringToStringMap values = prefs.getValues(settings);
		values.put(EDITOR_FONT, EditorPrefs.encodeFont(UISupport
				.getEditorFont()));
		form = new FormBuilder(parent, getNumColumns(), values);
		buildForm(form);
		return form.getPage();
	}

	public boolean performOk() {
		Settings settings = SoapUI.getSettings();

		prefs.storeValues(form.getFormValues(), settings);
		String font = form.getFormValues().get(EDITOR_FONT);
		if( font != null )
			settings.setString( UISettings.EDITOR_FONT, font );


		try {
			SoapUI.saveSettings();
			PropertyExpansionUtils.saveGlobalProperties();
		} catch (Exception e) {
			SoapUIActivator.logError("Could not save settings", e);
		}

		return true;
	}
}
