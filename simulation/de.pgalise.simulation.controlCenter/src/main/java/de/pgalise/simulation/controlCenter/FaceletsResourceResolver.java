/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter;

import java.net.URL;
import javax.faces.view.facelets.ResourceResolver;

/**
 *
 * @author richter
 */
public class FaceletsResourceResolver extends ResourceResolver {

  private ResourceResolver parent;
  private String basePath;

  public FaceletsResourceResolver(ResourceResolver parent) {
    this.parent = parent;
    this.basePath = "/META-INF/resources"; // TODO: Make configureable?
  }

  @Override
  public URL resolveUrl(String path) {
    URL url = parent.resolveUrl(path); // Resolves from WAR.

    if (url == null) {
      url = getClass().getResource(basePath + path); // Resolves from JAR.
    }

    return url;
  }

}
