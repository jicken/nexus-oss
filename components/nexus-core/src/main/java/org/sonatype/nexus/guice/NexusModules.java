/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2013 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.nexus.guice;

import javax.servlet.ServletContext;

import org.sonatype.nexus.web.TemplateRenderer;
import org.sonatype.nexus.web.internal.BaseUrlHolderFilter;
import org.sonatype.nexus.web.internal.ErrorPageFilter;
import org.sonatype.security.SecuritySystem;
import org.sonatype.security.web.guice.SecurityWebModule;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;
import com.yammer.metrics.guice.InstrumentationModule;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.eclipse.sisu.inject.DefaultRankingFunction;
import org.eclipse.sisu.inject.RankingFunction;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.name.Names.named;

/**
 * Nexus guice modules.
 *
 * @since 2.4
 */
public class NexusModules
{
  /**
   * Nexus common guice module.
   */
  public static class CommonModule
      extends AbstractModule
  {
    @Override
    protected void configure() {
      install(new ShiroAopModule());
      install(new InstrumentationModule());
    }
  }

  /**
   * Nexus core guice module.
   */
  public static class CoreModule
      extends AbstractModule
  {
    private final ServletContext servletContext;

    public CoreModule(final ServletContext servletContext) {
      this.servletContext = checkNotNull(servletContext);
    }

    @Override
    protected void configure() {
      // HACK: Re-bind servlet-context instance with a name to avoid backwards-compat warnings from guice-servlet
      bind(ServletContext.class).annotatedWith(named("nexus")).toInstance(servletContext);

      install(new CommonModule());

      install(new ServletModule()
      {
        @Override
        protected void configureServlets() {
          filter("/*").through(BaseUrlHolderFilter.class);
          filter("/*").through(ErrorPageFilter.class);

          // our configuration needs to be first-most when calculating order (some fudge room for edge-cases)
          bind(RankingFunction.class).toInstance(new DefaultRankingFunction(0x70000000));
        }
      });

      install(new SecurityWebModule(servletContext, true));
    }
  }

  /**
   * Nexus plugin guice module.
   */
  public static class PluginModule
      extends AbstractModule
  {
    @Override
    protected void configure() {
      install(new CommonModule());

      // handle some edge-cases for commonly used servlet-based components which need a bit more configuration
      // so that sisu/guice can find the correct bindings inside of plugins
      requireBinding(SecuritySystem.class);
      requireBinding(FilterChainResolver.class);
      requireBinding(TemplateRenderer.class);
    }
  }
}
