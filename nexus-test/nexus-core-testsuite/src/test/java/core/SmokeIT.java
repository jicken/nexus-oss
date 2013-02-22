/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.sonatype.nexus.client.core.NexusClient;
import org.sonatype.nexus.client.core.NexusStatus;
import org.sonatype.sisu.litmus.testsupport.group.Smoke;
import core.NexusCoreITSupport;

/**
 * Most basic IT just checking is bundle alive at all.
 * 
 * @since 2.4
 */
@Category( Smoke.class )
public class SmokeIT
    extends NexusCoreITSupport
{
    /**
     * Constructor.
     * 
     * @param nexusBundleCoordinates
     */
    public SmokeIT( final String nexusBundleCoordinates )
    {
        super( nexusBundleCoordinates );
    }

    /**
     * Test that only verifies that Nexus reports itself (the status resource actually, used by {@link NexusClient}) as
     * expected.
     */
    @Test
    public void verifyNexusReportsAsHealthyAndCorrect()
    {
        final NexusStatus nexusStatus = client().getStatus();

        assertThat( nexusStatus, is( notNullValue() ) );
        assertThat( nexusStatus.isFirstStart(), is( true ) ); // should be true
        assertThat( nexusStatus.isInstanceUpgraded(), is( false ) ); // should be false
        // TODO: nexus version we are running?
        // assertThat( nexusStatus.getVersion(), is( "1.0" ) ); // version
        assertThat( nexusStatus.getEditionShort(), equalTo( "OSS" ) );
    }
}
