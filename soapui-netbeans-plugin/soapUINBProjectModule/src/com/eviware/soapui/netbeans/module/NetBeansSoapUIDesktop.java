/*
 *  soapUI, copyright (C) 2004-2011 smartbear.com
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the
 *  terms of version 2.1 of the GNU Lesser General Public License as published by
 *  the Free Software Foundation.
 *  For the avoidance of doubt, eviware elects not to use any
 *  later versions of the LGPL License.
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details at gnu.org.
 *
 *
 *  -------------------------------------------------------------------------------------
 *  If soapUI is distributed with products from Sun, note the following:
 *  -------------------------------------------------------------------------------------
 *
 *  For the avoidance of doubt, except that if any license choice other than
 *  GPL or LGPL is available it will apply instead,
 *  Sun elects to use only the General Public License version 2 (GPLv2) at
 *  this time for any software where a choice of GPL
 *  license versions is made available with the language indicating that
 *  GPLv2 or any later version may be used, or where a
 *  choice of which version of the GPL is applied is otherwise unspecified.
 */


package com.eviware.soapui.netbeans.module;

import com.eviware.soapui.SoapUIExtensionClassLoader;
import com.eviware.soapui.SoapUIExtensionClassLoader.SoapUIClassLoaderState;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.util.PanelBuilderRegistry;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.ui.desktop.AbstractSoapUIDesktop;
import com.eviware.soapui.ui.desktop.DesktopPanel;
import com.eviware.soapui.ui.desktop.SoapUIDesktop;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.openide.windows.TopComponent;

/**
 * SoapUIDesktop implementation for Intellij
 */

public class NetBeansSoapUIDesktop extends AbstractSoapUIDesktop
{
   private Map<DesktopPanel, ModelItemTopComponent> desktopPanelToFileMap = new HashMap<DesktopPanel, ModelItemTopComponent>();
   private Map<ModelItem, DesktopPanel> modelItemToDesktopPanelMap = new HashMap<ModelItem, DesktopPanel>();
   
   public NetBeansSoapUIDesktop ( Workspace workspace )
   {
      super ( workspace );
   }
   
   public boolean closeDesktopPanel ( DesktopPanel desktopPanel )
   {
      final ModelItemTopComponent vf = desktopPanelToFileMap.get( desktopPanel );
      if( vf != null )
      {
          if( SwingUtilities.isEventDispatchThread())
            vf.close ();
          else
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                   vf.close ();
                }
            });
         desktopPanelToFileMap.remove( desktopPanel );
      }
      
      return true;
   }
   
   public boolean hasDesktopPanel ( ModelItem modelItem )
   {
      return modelItemToDesktopPanelMap.containsKey ( modelItem );
   }
   
   public DesktopPanel showDesktopPanel ( ModelItem modelItem )
   {
       SoapUIClassLoaderState state = SoapUIExtensionClassLoader.ensure();
      PanelBuilder panelBuilder = PanelBuilderRegistry.getPanelBuilder( modelItem );
      
      try
      {
      if( modelItemToDesktopPanelMap.containsKey ( modelItem ) )
      {
         DesktopPanel desktopPanel = modelItemToDesktopPanelMap.get ( modelItem );
         ModelItemTopComponent topComponent = desktopPanelToFileMap.get( desktopPanel );
         topComponent.requestActive ();
         return desktopPanel;
      }
      else if (panelBuilder != null && panelBuilder.hasDesktopPanel ())
      {
         DesktopPanel desktopPanel = showDesktopPanel ( panelBuilder.buildDesktopPanel( modelItem ) );
         modelItemToDesktopPanelMap.put ( modelItem, desktopPanel );
         return desktopPanel;
      }
      else
         Toolkit.getDefaultToolkit ().beep ();
      
      return null;
      }
      finally
      {
          state.restore();
      }
   }
   
   public boolean closeDesktopPanel ( ModelItem modelItem )
   {
      DesktopPanel desktopPanel = modelItemToDesktopPanelMap.get ( modelItem );
      if( desktopPanel != null )
      {
         closeDesktopPanel ( desktopPanel );
         modelItemToDesktopPanelMap.remove ( modelItem );
      }
      
      return true;
   }
   
   public DesktopPanel[] getDesktopPanels ()
   {
      return desktopPanelToFileMap.keySet ().toArray ( new DesktopPanel[desktopPanelToFileMap.size ()] );
   }
   
   public DesktopPanel getDesktopPanel ( ModelItem modelItem )
   {
      return modelItemToDesktopPanelMap.get ( modelItem );
   }
   
   public DesktopPanel showDesktopPanel ( DesktopPanel desktopPanel )
   {
      ModelItemTopComponent vf = new ModelItemTopComponent ( desktopPanel );
      desktopPanelToFileMap.put( desktopPanel, vf );
      
      vf.open ();
      vf.requestActive ();
      
      return desktopPanel;
   }

    public JComponent getDesktopComponent() {
        return null;
    }

    public void transferTo(SoapUIDesktop soapUIDesktop) {
    }

    public boolean closeAll() {
        
        while( !desktopPanelToFileMap.isEmpty() )
        {
            closeDesktopPanel( desktopPanelToFileMap.keySet().iterator().next() );
        }

        modelItemToDesktopPanelMap.clear();
        return true;
    }

    public void maximize(DesktopPanel arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
   
   private class ModelItemTopComponent extends TopComponent
   {
      private DesktopPanel desktopPanel;
      
      public ModelItemTopComponent( DesktopPanel desktopPanel )
      {
         this.desktopPanel = desktopPanel;
       
         setLayout ( new BorderLayout() );
         add( desktopPanel.getComponent (), BorderLayout.CENTER );
         
         setName (desktopPanel.getModelItem() == null ? desktopPanel.getTitle() : desktopPanel.getModelItem().getName());
      }
      
      public DesktopPanel getDesktopPanel()
      {
         return desktopPanel;
      }

      protected void componentActivated()
      {
         super.componentActivated();
         
         fireDesktopPanelSelected( desktopPanel );
      }
      
      protected void componentClosed()
      {
         super.componentClosed ();
         
         desktopPanel.onClose ( false );
         
         desktopPanelToFileMap.remove( desktopPanel );
         if( desktopPanel.getModelItem() != null )
         {
            modelItemToDesktopPanelMap.remove( desktopPanel.getModelItem() );
         }
      }
      
      public int getPersistenceType()
      {
         return PERSISTENCE_NEVER;
      }
   }

    public void minimize(DesktopPanel arg0) {
    }
}
