/*
 * IzPack - Copyright 2001-2007 Julien Ponge, All Rights Reserved.
 * 
 * http://izpack.org/
 * http://developer.berlios.de/projects/izpack/
 * 
 * Copyright 2003 Jonathan Halliday
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.panels;

import com.izforge.izpack.adaptator.IXMLElement;
import com.izforge.izpack.adaptator.impl.XMLElementImpl;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.PanelAutomation;

/**
 * Functions to support automated usage of the TargetPanel
 * 
 * @author Jonathan Halliday
 * @author Julien Ponge
 */
public class TargetPanelAutomationHelper implements PanelAutomation
{

    /**
     * Asks to make the XML panel data.
     * 
     * @param idata The installation data.
     * @param panelRoot The tree to put the data in.
     */
    public void makeXMLData(AutomatedInstallData idata, IXMLElement panelRoot)
    {
        // Installation path markup
        IXMLElement ipath = new XMLElementImpl("installpath");
        // check this writes even if value is the default,
        // because without the constructor, default does not get set.
        ipath.setContent(idata.getInstallPath());

        // Checkings to fix bug #1864
        IXMLElement prev = panelRoot.getFirstChildNamed("installpath");
        if (prev != null) panelRoot.removeChild(prev);

        panelRoot.addChild(ipath);
    }

    /**
     * Asks to run in the automated mode.
     * 
     * @param idata The installation data.
     * @param panelRoot The XML tree to read the data from.
     * 
     * @return always true.
     */
    public void runAutomated(AutomatedInstallData idata, IXMLElement panelRoot)
    {
        // We set the installation path
        IXMLElement ipath = panelRoot.getFirstChildNamed("installpath");
        idata.setInstallPath(ipath.getContent());
    }


}
