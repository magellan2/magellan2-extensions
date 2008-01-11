/*
 *  Copyright (C) 2007-2008 Steffen Mecke
 *
 * This file is part of UTF2ISO. See the file LICENSE.txt for the licensing information
 * applying to this file.
 */

package magellan.ext.utf2iso;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class Messages {
//  private static final String BUNDLE_NAME = Messages.class.getPackage().toString()+".messages"; //$NON-NLS-1$
  private static final String BUNDLE_NAME = "magellan.ext.utf2iso.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  private Messages() {
  }

  public static String getString(String key) {
    try {
      return RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}
